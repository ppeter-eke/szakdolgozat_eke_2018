/*******************************************************************************
 * Szakdolgozat
 * Raktári információs rendszer fejlesztése Java nyelven
 *
 * Eszterházy Károly Egyetem
 * Alkalmazott Informatika Tanszék
 *
 * Készítette: Pálosi Péter gazdaságinformatikus BSc levelező tagozat
 *
 * Gyöngyös, 2018
 *******************************************************************************/


// https://github.com/ajay04/Barcode_Java/blob/master/src/UI/MainViewController.java //

package application.administrator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class Barcode {

	public Barcode() {

	}

	public void positionCode(String text) throws FileNotFoundException, IOException {
		Code128Bean code128 = new Code128Bean();
		code128.setHeight(15f);
		code128.setModuleWidth(0.3);
		code128.setQuietZone(10);
		code128.doQuietZone(true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY,
				false, 0);
		code128.generateBarcode(canvas, text);
		canvas.finish();

		

		FileOutputStream fos = new FileOutputStream("barcode_" + text + "_.png");
		fos.write(baos.toByteArray());
		fos.flush();
		fos.close();
	}
	
	
	public void palletCode(String palletId, String partNo, double quantity) throws IOException {
		
		Code128Bean code128 = new Code128Bean();
		code128.setHeight(15f);
		code128.setModuleWidth(0.3);
		code128.setQuietZone(10);
		code128.doQuietZone(true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY,
				false, 0);
		code128.generateBarcode(canvas,palletId);
		canvas.finish();

		

		FileOutputStream fos = new FileOutputStream("barcode_" + palletId + "_" + partNo + "_" + quantity +  "_.png");
		fos.write(baos.toByteArray());
		fos.flush();
		fos.close();
		
		
		File file = new File("barcode_" + palletId + "_" + partNo + "_" + quantity + "_.png");
		
		BufferedImage bi = ImageIO.read(file);
		
		
	    Graphics2D g2d = bi.createGraphics();
	    Font font = new Font("Arial", Font.PLAIN, 28);
	    g2d.setFont(font);
	    g2d.setColor(Color.BLACK);
	    g2d.drawString(partNo, 45, 210);
	    
	    g2d.drawString(quantity + "", 650, 210);
	    g2d.dispose();
	    
	    ImageIO.write(bi, "png", new File("barcode_" + palletId + "_" + partNo + "_" + quantity + "_.png"));
		
	}

}
