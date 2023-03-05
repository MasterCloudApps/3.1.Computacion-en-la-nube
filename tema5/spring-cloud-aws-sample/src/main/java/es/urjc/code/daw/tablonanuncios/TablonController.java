package es.urjc.code.daw.tablonanuncios;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Controller
public class TablonController {

	@Autowired
	private AnunciosRepository repository;
	
	private S3Client s3;

	private S3Utilities utilities;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@PostConstruct
	public void init() {
		this.s3 = S3Client.builder().build(); 
        this.utilities = s3.utilities();
		repository.save(new Anuncio("Pepe", "Hola caracola", "A description"));
		repository.save(new Anuncio("Juan", "Hola caracola", "A description"));
	}

	@GetMapping("/")
	public String tablon(Model model, Pageable page) {

		model.addAttribute("anuncios", repository.findAll(page));

		return "tablon";
	}

	@PostMapping("/anuncio/nuevo")
	public String nuevoAnuncio(Model model, 
			@RequestParam String nombre,
			@RequestParam String asunto,
			@RequestParam String comentario,
			@RequestParam String filename,
			@RequestParam MultipartFile file) {

        if (!file.isEmpty()) {
            try {

				s3.putObject(
					PutObjectRequest.builder()
						.bucket(bucket)
						.key(filename)
						.acl(ObjectCannedACL.PUBLIC_READ)
						.build(), 
					RequestBody.fromBytes(file.getBytes())
				);
                
            } catch (Exception e) {
            	model.addAttribute("message", "You failed to upload " + filename + " => " + e.getMessage());
                return "error";
            }
        } else {
        	model.addAttribute("message", "You failed to upload " + filename + " because the file was empty.");
            return "error";
        }

        Anuncio anuncio = new Anuncio(nombre, asunto, comentario);

		URL url = utilities.getUrl(
            GetUrlRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build()
        );

        anuncio.setFoto(url);

		repository.save(anuncio);

        return "anuncio_guardado";

	}

	@GetMapping("/anuncio/{id}")
	public String verAnuncio(Model model, @PathVariable long id) {
		
		Anuncio anuncio = repository.findById(id).get();

		model.addAttribute("hasFoto", anuncio.getFoto() != null);
		model.addAttribute("anuncio", anuncio);

		return "ver_anuncio";
	}
	
}