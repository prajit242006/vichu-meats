package com.vichumeats.service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
  @Value("${app.uploads}")
  String directory;

  public String save(MultipartFile file) throws IOException {
    if (file.isEmpty()) return null;
    if (file.getSize() > 3 * 1024 * 1024)
      throw new IllegalArgumentException("Image must be under 3 MB.");
    String type = file.getContentType();
    if (!Set.of("image/jpeg", "image/png").contains(type == null ? "" : type))
      throw new IllegalArgumentException("Use JPG or PNG.");
    BufferedImage image;
    try (var input = ImageIO.createImageInputStream(file.getInputStream())) {
      var readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new IllegalArgumentException("Invalid image.");
      var reader = readers.next();
      try {
        if (!Set.of("png", "jpeg", "jpg")
            .contains(reader.getFormatName().toLowerCase(java.util.Locale.ROOT)))
          throw new IllegalArgumentException("Use a real JPG or PNG image.");
        reader.setInput(input);
        if (reader.getWidth(0) > 4000 || reader.getHeight(0) > 4000)
          throw new IllegalArgumentException("Maximum image dimensions: 4000 x 4000.");
        image = reader.read(0);
      } finally {
        reader.dispose();
      }
    }
    if (image == null) throw new IllegalArgumentException("Invalid image.");
    Path dir = Path.of(directory).toAbsolutePath().normalize();
    Files.createDirectories(dir);
    String name = UUID.randomUUID() + ".png";
    ImageIO.write(image, "png", dir.resolve(name).toFile());
    return name;
  }
}
