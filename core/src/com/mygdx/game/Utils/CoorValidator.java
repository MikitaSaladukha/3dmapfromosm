package com.mygdx.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.vividsolutions.jts.geom.Coordinate;
import de.topobyte.adt.geo.BBox;


public class CoorValidator {

    public static boolean validateCoor(BBox bBox, Coordinate cor){
        boolean res = false;

        if (cor.y <= bBox.getLat1()+0.001 & cor.y >= bBox.getLat2()-0.001)
            if (cor.x <= bBox.getLon2()+0.001 & cor.x >= bBox.getLon1()-0.001)
                res = true;

        return res;
    }
    public static boolean validateCoor(Vector3 pos, Coordinate cor){
        boolean res = false;

        if (Math.sqrt((pos.x- cor.x)*(pos.x- cor.x)+ (pos.z- cor.y)*(pos.z- cor.y)) > 200.0f)
            res = true;

        return res;
    }
}
