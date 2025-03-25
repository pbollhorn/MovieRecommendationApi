package dat.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dat.dto.FrontendMovieDto;
import dat.dto.GenreDto;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dat.dao.MovieDao;

import java.util.List;

public class MovieController implements IController {

    private final MovieDao movieDao;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final SecurityController securityController;

    public MovieController(EntityManagerFactory emf, SecurityController securityController) {
        movieDao = MovieDao.getInstance(emf);
        this.securityController=securityController;
    }

    @Override
    public void create(Context ctx) {

    }

    @Override
    public void getById(Context ctx) {

    }

    @Override
    public void getAll(Context ctx) {

    }

    @Override
    public void update(Context ctx) {

    }

    @Override
    public void delete(Context ctx) {

    }

    public void search(Context ctx) {

        String text = ctx.bodyAsClass(JsonNode.class).get("text").asText();

        List<FrontendMovieDto> frontendMovieDtos = movieDao.getMoviesByTextInTitle(text);

        ctx.json(frontendMovieDtos);

    }


    public void createRating(Context ctx) {

        int movieId = Integer.parseInt(ctx.pathParam("id"));

        Boolean rating = ctx.bodyAsClass(JsonNode.class).get("rating").asBoolean();

        System.out.println("HALLÅ FRA id: " + movieId);
        System.out.println("BOOLEAN: " + rating);

        UserDTO verifiedTokenUser = securityController.getUserFromToken(ctx);
        System.out.println(verifiedTokenUser);

    }


    public void test(Context ctx) {

        GenreDto genreDto = new GenreDto(5, "Five");

        ctx.json(genreDto);
//        ctx.status(404);

    }


}
