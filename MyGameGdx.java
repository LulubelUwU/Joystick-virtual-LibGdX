package com.mycompany.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class MyGdxGame extends ApplicationAdapter {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private ModelInstance cubeInstance;
    private float cubeX, cubeY, cubeZ;
    private ShapeRenderer shapeRenderer;
    private Vector2 touchPos;
    private float outerSquareSize = 150; // Cuadrado blanco más grande
    private float innerSquareSize = 30;
    private float outerSquareX = 40;
    private float outerSquareY = 40;
    private float innerSquareX;
    private float innerSquareY;

    @Override
    public void create() {
        // Configurar la cámara
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 10f, 10f); // Posición más baja y hacia adelante
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        // Configurar el entorno
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        // Crear el modelo del cubo
        ModelBuilder modelBuilder = new ModelBuilder();
        Model cubeModel = modelBuilder.createBox(2f, 2f, 2f,
												 new Material(ColorAttribute.createDiffuse(Color.RED)),
												 Usage.Position | Usage.Normal);
        cubeInstance = new ModelInstance(cubeModel);
        cubeX = 0;
        cubeY = 0;
        cubeZ = 0;

        modelBatch = new ModelBatch();

        // Configurar el joystick virtual
        shapeRenderer = new ShapeRenderer();
        touchPos = new Vector2();
        // Posición inicial del cuadrado pequeño en el centro del cuadrado grande
        innerSquareX = outerSquareX + (outerSquareSize - innerSquareSize) / 2;
        innerSquareY = outerSquareY + (outerSquareSize - innerSquareSize) / 2;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Obtener la posición del toque
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            innerSquareX = touchPos.x - innerSquareSize / 2;
            innerSquareY = touchPos.y - innerSquareSize / 2;

            // Limitar el cuadrado pequeño a estar dentro del cuadrado grande
            if (innerSquareX < outerSquareX) innerSquareX = outerSquareX;
            if (innerSquareY < outerSquareY) innerSquareY = outerSquareY;
            if (innerSquareX + innerSquareSize > outerSquareX + outerSquareSize) innerSquareX = outerSquareX + outerSquareSize - innerSquareSize;
            if (innerSquareY + innerSquareSize > outerSquareY + outerSquareSize) innerSquareY = outerSquareY + outerSquareSize - innerSquareSize;
        } else {
            // Reposicionar el cuadrado pequeño en el centro del cuadrado grande
            innerSquareX = outerSquareX + (outerSquareSize - innerSquareSize) / 2;
            innerSquareY = outerSquareY + (outerSquareSize - innerSquareSize) / 2;
        }

        // Actualizar la posición del cubo basado en el joystick
        float deltaX = (innerSquareX + innerSquareSize / 2 - (outerSquareX + outerSquareSize / 2)) / (outerSquareSize / 2);
        float deltaY = (innerSquareY + innerSquareSize / 2 - (outerSquareY + outerSquareSize / 2)) / (outerSquareSize / 2);
        cubeX += deltaX * Gdx.graphics.getDeltaTime() * 5;
        cubeZ -= deltaY * Gdx.graphics.getDeltaTime() * 5; // Invertir el eje Z

        cubeInstance.transform.setToTranslation(cubeX, cubeY, cubeZ);

        modelBatch.begin(camera);
        modelBatch.render(cubeInstance, environment);
        modelBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibuja el cuadrado grande
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(outerSquareX, outerSquareY, outerSquareSize, outerSquareSize);

        // Dibuja el cuadrado pequeño
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(innerSquareX, innerSquareY, innerSquareSize, innerSquareSize);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        shapeRenderer.dispose();
    }
}
