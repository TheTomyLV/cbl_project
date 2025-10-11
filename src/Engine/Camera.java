package Engine;

public class Camera {
    public static Camera currentCamera;

    public float x = 0f;
    public float y = 0f;

    Camera() {
        currentCamera = this;
    }
}