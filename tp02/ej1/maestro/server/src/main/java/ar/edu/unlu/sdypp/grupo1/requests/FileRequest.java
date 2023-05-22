package ar.edu.unlu.sdypp.grupo1.requests;

import lombok.Data;

@Data
public class FileRequest {
    private String name;
    private Long sizeInBytes;
    private String hash;
}