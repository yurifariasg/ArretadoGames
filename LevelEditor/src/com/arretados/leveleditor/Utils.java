/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor;

/**
 *
 * @author Bruno
 */
public class Utils {
    
    public static float convertPixelToMeter(float pixels){
        float calc;
        calc = pixels/GameCanvas.METER_TO_PIXELS;
        return calc;
    }
    
}
