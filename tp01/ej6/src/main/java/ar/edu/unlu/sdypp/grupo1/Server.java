package ar.edu.unlu.sdypp.grupo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class Server{

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @GetMapping("/suma")
    public int[] suma(@RequestParam int[] vector1, @RequestParam int[] vector2) {
        int[] resultado = (
            (vector1.length >= vector2.length)
            ? new int[vector1.length]
            : new int[vector2.length]
        );

        int v1;
        int v2;
        for (int i = 0; i < resultado.length; i++) {
            v1 = ((i < vector1.length) ? vector1[i] : 0);
            v2 = ((i < vector2.length) ? vector2[i] : 0);
            resultado[i] = v1 + v2;
        }

        return resultado;
    }

    @GetMapping("/resta")
    public int[] resta(@RequestParam int[] vector1, @RequestParam int[] vector2) {
        int[] resultado = (
            (vector1.length >= vector2.length)
            ? new int[vector1.length] :
            new int[vector2.length]
        );
        for (int i = 0; i < resultado.length; i++) {
            int v1 = ((vector1.length < i) ? vector1[i] : 0);
            int v2 = ((vector2.length < i) ? vector2[i] : 0);
            resultado[i] = v1 - v2;
        }
        
        return resultado;
    }
}
