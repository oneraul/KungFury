package KungFury;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class KungFury {
    public static void main(String[] args) {
    	new LwjglApplication(new App(), "Kung Fury", 800, 600);
    }
}

class App extends ApplicationAdapter {
	
	private ShapeRenderer shaper;
	private float[] lines;
	private int screen_width, screen_height;
	private int number_of_lines_horizontal, number_of_lines_vertical;
	private float a, separacion_far, separacion_near, far, near;
	private Stage stage;
	private Color color_bg, color_lines;
	
	@Override
	public void create() {
		shaper = new ShapeRenderer();
		screen_width = Gdx.graphics.getWidth();
		screen_height = Gdx.graphics.getHeight();
		color_bg = Color.BLACK;
		color_lines = Color.MAGENTA;
		shaper.setColor(color_lines);
		
		a = 150;
		
		number_of_lines_horizontal = 10;
		number_of_lines_vertical = 5;
		
		separacion_far = screen_width * 0.1f;
		separacion_near = screen_width * 0.3f;
		
		far = screen_height * 0.4f;
		near = screen_height * 0.1f;
		
		setupLines();
		setUI();
	}
	
	private void setupLines() {
		lines = new float[number_of_lines_horizontal];
		float final_t = (float)Math.sqrt((near-far)/(-0.5f*a));
		for(int i = 0; i < lines.length; i++)
			lines[i] = i * final_t/number_of_lines_horizontal;
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(color_bg.r, color_bg.g, color_bg.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float dt = Gdx.graphics.getDeltaTime();
		
		shaper.begin(ShapeRenderer.ShapeType.Line);
		for(int i = 0; i < lines.length; i++) {
			lines[i] += dt;
			
			// x = x0 + v0*dt + 1/2*a*t^2
			float y = far - 0.5f * a * lines[i] * lines[i];
			if(y < near) {
				lines[i] = 0;
				continue;
			}
			
			shaper.line(0, y, screen_width, y);
		}

		shaper.line(0, near, screen_width, near);
		shaper.line(0, far,  screen_width, far);

		if(number_of_lines_vertical > 0) shaper.line(screen_width/2, near, screen_width/2, far);
		for(int i = 0; i < number_of_lines_vertical; i++) {
			shaper.line(screen_width/2 - separacion_near*i, near, screen_width/2 - separacion_far*i, far);
			shaper.line(screen_width/2 + separacion_near*i, near, screen_width/2 + separacion_far*i, far);
		}
		shaper.end();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void dispose() {
		shaper.dispose();
		stage.dispose();
		VisUI.dispose();
	}
	
	private void setUI() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		VisUI.load();
		
		Opciones opciones = new Opciones();
		
		VisTextButton button = new VisTextButton("Opciones");
		button.setPosition(3, 3);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				opciones.setVisible(!opciones.isVisible());
			}
		});
		
		stage.addActor(button);
		stage.addActor(opciones);
	}
	
	class Opciones extends VisWindow {
		public Opciones() {
			super("Opciones");
			setPosition(10, screen_height-90-getHeight());
			setKeepWithinStage(false);
			setSize(100, 235);
			setVisible(false);
			
			VisSlider nearSlider = new VisSlider(0, 1, 0.01f, true);
			new Tooltip(nearSlider, "Near plane");
			nearSlider.setValue(near / screen_height);
			nearSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					near = screen_height * nearSlider.getValue();
					setupLines();
				}
			});
			
			VisSlider farSlider = new VisSlider(0, 1, 0.01f, true);
			new Tooltip(farSlider, "Horizonte");
			farSlider.setValue(far / screen_height);
			farSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					far = screen_height * farSlider.getValue();
					setupLines();
				}
			});
			
			VisSlider separacion_nearSlider = new VisSlider(0, 1, 0.01f, true);
			new Tooltip(separacion_nearSlider, "Separacion entre las lineas en el near plane");
			separacion_nearSlider.setValue(separacion_near / screen_width);
			separacion_nearSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					separacion_near = screen_width * separacion_nearSlider.getValue();
				}
			});
			
			VisSlider separacion_farSlider = new VisSlider(0, 1, 0.01f, true);
			new Tooltip(separacion_farSlider, "Separacion entre las lineas en el horizonte");
			separacion_farSlider.setValue(separacion_far / screen_width);
			separacion_farSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					separacion_far = screen_width * separacion_farSlider.getValue();
				}
			});
			
			VisSlider number_of_lines_verticalSlider = new VisSlider(0, 20, 1, true);
			new Tooltip(number_of_lines_verticalSlider, "Numero de lineas verticales");
			number_of_lines_verticalSlider.setValue(number_of_lines_vertical);
			number_of_lines_verticalSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					number_of_lines_vertical = (int)number_of_lines_verticalSlider.getValue();
				}
			});
			
			VisSlider number_of_lines_horizontalSlider = new VisSlider(0, 20, 1, true);
			new Tooltip(number_of_lines_horizontalSlider, "Numero de lineas horizontales");
			number_of_lines_horizontalSlider.setValue(number_of_lines_horizontal);
			number_of_lines_horizontalSlider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					number_of_lines_horizontal = (int)number_of_lines_horizontalSlider.getValue();
					setupLines();
				}
			});
			
			ColorPicker picker_bg = new ColorPicker(new ColorPickerAdapter() {
			    @Override
			    public void finished(Color newColor) {
					color_bg = newColor;
			    }
			});
			
			VisTextButton picker_bg_button = new VisTextButton("background");
			picker_bg_button.addListener(new ChangeListener() {
			    @Override
			    public void changed (ChangeEvent event, Actor actor) {
			        getStage().addActor(picker_bg.fadeIn());
			    }
			});
			
			ColorPicker picker_lines = new ColorPicker(new ColorPickerAdapter() {
			    @Override
			    public void finished(Color newColor) {
					color_lines = newColor;
					shaper.setColor(color_lines);
			    }
			});
			
			VisTextButton picker_lines_button = new VisTextButton("lines");
			picker_lines_button.addListener(new ChangeListener() {
			    @Override
			    public void changed (ChangeEvent event, Actor actor) {
			        getStage().addActor(picker_lines.fadeIn());
			    }
			});
			
			add(nearSlider);
			add(farSlider);
			add(separacion_nearSlider);
			add(separacion_farSlider);
			add(number_of_lines_verticalSlider);
			add(number_of_lines_horizontalSlider);
			
			row();
			
			add(picker_bg_button).colspan(6).fillX().padTop(5).row();
			add(picker_lines_button).colspan(6).fillX().padTop(5);
		}
	}
}