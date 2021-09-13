package birsy.clinker.client.render.util;

public interface IPosablePart {
    float getRotationPointX();
    float getRotationPointY();
    float getRotationPointZ();

    float getRotateAngleX();
    float getRotateAngleY();
    float getRotateAngleZ();

    float getScaleX();
    float getScaleY();
    float getScaleZ();

    void setRotationPointX(float value);
    void setRotationPointY(float value);
    void setRotationPointZ(float value);

    void setRotateAngleX(float value);
    void setRotateAngleY(float value);
    void setRotateAngleZ(float value);

    void setScaleX(float value);
    void setScaleY(float value);
    void setScaleZ(float value);
}
