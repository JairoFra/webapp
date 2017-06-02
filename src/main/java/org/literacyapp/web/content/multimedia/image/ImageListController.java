package org.literacyapp.web.content.multimedia.image;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
import org.literacyapp.dao.ImageDao;
import org.literacyapp.model.Contributor;
import org.literacyapp.model.content.multimedia.Image;
import org.literacyapp.model.enums.Locale;
import org.literacyapp.model.enums.content.ImageFormat;
import org.literacyapp.util.ImageColorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/content/multimedia/image/list")
public class ImageListController {
    
    private final Logger logger = Logger.getLogger(getClass());
    
    @Autowired
    private ImageDao imageDao;

    @RequestMapping(method = RequestMethod.GET)
    public String handleRequest(Model model, HttpSession session) {
    	logger.info("handleRequest");
        
        Contributor contributor = (Contributor) session.getAttribute("contributor");
        
        // To ease development/testing, auto-generate Images
        List<Image> imagesGenerated = generateImages(contributor.getLocale());
        for (Image image : imagesGenerated) {
            Image existingImage = imageDao.read(image.getTitle(), image.getLocale());
            if (existingImage == null) {
                ImageFormat imageFormat = ImageFormat.PNG;
                image.setContentType("image/png");
                String fileName = image.getTitle() + "." + imageFormat.toString().toLowerCase();
                logger.info("Looking up file \"" + image.getTitle() + "." + imageFormat.toString().toLowerCase() + "\"...");
                URL url = getClass().getResource(fileName);
                logger.info("url.getPath(): " + url.getPath());
                try {
                    byte[] imageBytes = IOUtils.toByteArray(url);
                    image.setBytes(imageBytes);
                    image.setImageFormat(imageFormat);
                    int[] dominantColor = ImageColorHelper.getDominantColor(image.getBytes());
                    image.setDominantColor("rgb(" + dominantColor[0] + "," + dominantColor[1] + "," + dominantColor[2] + ")");
                    imageDao.create(image);
                } catch (IOException ex) {
                    logger.error(null, ex);
                }
            }
        }
        
        List<Image> images = imageDao.readAllOrdered(contributor.getLocale());
        model.addAttribute("images", images);

        return "content/multimedia/image/list";
    }
    
    private List<Image> generateImages(Locale locale) {
        List<Image> images = new ArrayList<>();
        
        Image coverImage175 = new Image();
        coverImage175.setLocale(locale);
        coverImage175.setTimeLastUpdate(Calendar.getInstance());
        coverImage175.setTitle("ASP_175_I_like_to_read_Page_01_Image_0001");
        images.add(coverImage175);
        
        Image coverImage55 = new Image();
        coverImage55.setLocale(locale);
        coverImage55.setTimeLastUpdate(Calendar.getInstance());
        coverImage55.setTitle("M_ASP_55_Too_small_Page_02_Image_0001");
        images.add(coverImage55);
        
        return images;
    }
}
