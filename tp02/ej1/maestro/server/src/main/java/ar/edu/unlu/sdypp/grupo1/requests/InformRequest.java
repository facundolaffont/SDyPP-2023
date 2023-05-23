package ar.edu.unlu.sdypp.grupo1.requests;

import java.util.List;
import lombok.Data;

@Data
public class InformRequest {
    private List<FileDescriptionRequest> files;
}