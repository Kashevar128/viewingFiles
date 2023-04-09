package ru.vin.viewingFiles.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vin.viewingFiles.services.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    @GetMapping("/photos")
    public List<String> getListPhoto() {
        return imageService.createClientConnect().getListFilesClient();
    }

}
