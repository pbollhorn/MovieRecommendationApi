package dat.controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dat.dao.MovieDao;
import dat.dto.FrontendMovieDto;

public class MovieController {

    private final MovieDao movieDao;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final SecurityController securityController;

    public MovieController(EntityManagerFactory emf, SecurityController securityController) {
        movieDao = MovieDao.getInstance(emf);
        this.securityController = securityController;
    }


    public void searchMovies(Context ctx) {

        String text = ctx.bodyAsClass(JsonNode.class).get("text").asText();

        List<FrontendMovieDto> frontendMovieDtos = movieDao.getMoviesByTextInTitleOrOrignalTitle(text);

        ctx.json(frontendMovieDtos);

    }


    public void getAllMoviesWithLikes(Context ctx) {

        int accountId = securityController.getAccountIdFromToken(ctx);

        System.out.println("endpointet er ramt");

        List<FrontendMovieDto> frontendMovieDtos = movieDao.getMoviesAndRatings(accountId);

        System.out.println("HALLO: " + frontendMovieDtos.size());
        ctx.json(frontendMovieDtos);

    }


    public void updateOrCreateMovieLike(Context ctx) {

        int accountId = securityController.getAccountIdFromToken(ctx);
        int movieId = Integer.parseInt(ctx.pathParam("id"));
        Boolean rating = ctx.bodyAsClass(JsonNode.class).get("rating").asBoolean();

        movieDao.updateOrCreateRating(accountId, movieId, rating);

    }


    public void deleteMovieLike(Context ctx) {

        int accountId = securityController.getAccountIdFromToken(ctx);
        int movieId = Integer.parseInt(ctx.pathParam("id"));

        movieDao.deleteRating(accountId, movieId);
    }


    public void getMovieRecommendations(Context ctx) {

        int accountId = securityController.getAccountIdFromToken(ctx);

        List<FrontendMovieDto> recommendations = movieDao.getRecommendations(accountId, 25);

        ctx.json(recommendations);

    }

}