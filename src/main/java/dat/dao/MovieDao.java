package dat.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dat.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import dat.entities.Account;
import dat.entities.AccountMovieRating;
import dat.entities.Movie;


public class MovieDao extends AbstractDao<Movie, Integer> {

    private static MovieDao instance;

    private MovieDao(EntityManagerFactory emf) {
        super(Movie.class, emf);
    }

    public static MovieDao getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new MovieDao(emf);
        }
        return instance;
    }

    public Set<Integer> getAllMovieIds() {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = "SELECT m.id FROM Movie m";
            TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
            return new HashSet<Integer>(query.getResultList());

        }

    }


    public List<MovieOverviewDto> searchMoviesOpen(String text, int limit) {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = """
                    SELECT NEW dat.dto.MovieOverviewDto(m) FROM Movie m
                    WHERE LOWER(m.title) LIKE :title OR LOWER(m.originalTitle) LIKE :title
                    ORDER BY m.title LIMIT :limit""";
            TypedQuery<MovieOverviewDto> query = em.createQuery(jpql, MovieOverviewDto.class);
            query.setParameter("title", "%" + text.toLowerCase() + "%");
            query.setParameter("limit", limit);
            return query.getResultList();

        }

    }

    public List<MovieOverviewDto> searchMovies(String text, int accountId, int limit) {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = """
                    SELECT NEW dat.dto.MovieOverviewDto(m,
                    (SELECT a.rating FROM AccountMovieRating a WHERE a.movie.id=m.id AND a.account.id=:accountId))
                    FROM Movie m WHERE LOWER(m.title) LIKE :title OR LOWER(m.originalTitle) LIKE :title
                    ORDER BY m.title LIMIT :limit""";

            TypedQuery<MovieOverviewDto> query = em.createQuery(jpql, MovieOverviewDto.class);
            query.setParameter("title", "%" + text.toLowerCase() + "%");
            query.setParameter("accountId", accountId);
            query.setParameter("limit", limit);
            return query.getResultList();

        }

    }

    // TODO: Need to figure out a way for this to work both with and without ratings
    public List<MovieOverviewDto> getMoviesWithPerson(int personId) {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = """
                    SELECT DISTINCT NEW dat.dto.MovieOverviewDto(m) FROM Movie m JOIN Credit c ON m.id=c.movie.id
                    WHERE c.person.id=:personId ORDER BY m.releaseDate""";
            TypedQuery<MovieOverviewDto> query = em.createQuery(jpql, MovieOverviewDto.class);
            query.setParameter("personId", personId);
            return query.getResultList();

        }

    }

    // TODO: Need to figure out a way for this to work both with and without ratings
    public List<MovieOverviewDto> getMoviesInCollection(int collectionId) {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = """
                    SELECT NEW dat.dto.MovieOverviewDto(m) FROM Movie m
                    WHERE m.collection.id=:collectionId
                    ORDER BY m.releaseDate""";
            TypedQuery<MovieOverviewDto> query = em.createQuery(jpql, MovieOverviewDto.class);
            query.setParameter("collectionId", collectionId);
            return query.getResultList();

        }

    }


    public List<MovieOverviewDto> getAllMoviesWithRating(int accountId) {

        try (EntityManager em = emf.createEntityManager()) {

            String jpql = """
                    SELECT NEW dat.dto.MovieOverviewDto(m, a.rating)
                    FROM AccountMovieRating a JOIN Movie m ON a.movie.id = m.id WHERE a.account.id =:accountId
                    ORDER BY m.title """;

            TypedQuery<MovieOverviewDto> query = em.createQuery(jpql, MovieOverviewDto.class);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        }

    }

    public MovieDetailsDto getMovieDetails(int movieId) {

        try (EntityManager em = emf.createEntityManager()) {

            // Find Movie entity, TODO: Some error handling in case m is null
            Movie m = em.find(Movie.class, movieId);

            // Find persons list
            String jpql = """
                    SELECT NEW dat.dto.CreditDto(c.id, p.id, p.name, c.job, c.character)
                    FROM Person p JOIN Credit c ON c.person.id = p.id WHERE c.movie.id =:movieId
                    ORDER BY c.rankInMovie """;

            TypedQuery<CreditDto> query = em.createQuery(jpql, CreditDto.class);
            query.setParameter("movieId", movieId);
            List<CreditDto> credits = query.getResultList();

            return new MovieDetailsDto(m, credits);

        }

    }


    public void updateOrCreateMovieRating(int accountId, int movieId, boolean rating) {

        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            // First try to update existing rating
            String jpql = "UPDATE AccountMovieRating a SET a.rating =:rating WHERE a.account.id=:accountId AND a.movie.id = :movieId";
            Query query = em.createQuery(jpql);
            query.setParameter("accountId", accountId);
            query.setParameter("movieId", movieId);
            query.setParameter("rating", rating);
            int rowsAffected = query.executeUpdate();

            // Create new rating, if rating does not already exist
            if (rowsAffected == 0) {
                Account account = em.find(Account.class, accountId);
                Movie movie = em.find(Movie.class, movieId);
                em.persist(new AccountMovieRating(null, account, movie, rating));
            }

            em.getTransaction().commit();

        }

    }


    public void deleteMovieRating(int accountId, int movieId) {

        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "DELETE FROM AccountMovieRating a WHERE a.account.id = :accountId AND a.movie.id = :movieId";
            em.getTransaction().begin();
            Query query = em.createQuery(jpql);
            query.setParameter("accountId", accountId);
            query.setParameter("movieId", movieId);
            int rowsAffected = query.executeUpdate();
            em.getTransaction().commit();
        }

    }


    public List<MovieOverviewDto> getMovieRecommendations(int accountId, int limit) {

        try (EntityManager em = emf.createEntityManager()) {

            // Get list of id of movies, which the user have rated positively
            String jpql = "SELECT a.movie.id FROM AccountMovieRating a WHERE a.account.id = :accountId AND rating=TRUE";
            TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
            query.setParameter("accountId", accountId);
            List<Integer> movieIds = query.getResultList();

            // Get list of director ids which have made movies, which the user have rated positively
            jpql = "SELECT p.id FROM Person p JOIN Credit c ON c.person.id=p.id WHERE c.job='Director' AND c.movie.id IN :movieIds";
            query = em.createQuery(jpql, Integer.class);
            query.setParameter("movieIds", movieIds);
            List<Integer> directorIds = query.getResultList();

            // Select all movies from these directors
            jpql = "SELECT m.id FROM Movie m JOIN Credit c ON c.movie.id=m.id WHERE c.job='Director' AND c.person.id IN :directorIds";
            query = em.createQuery(jpql, Integer.class);
            query.setParameter("directorIds", directorIds);
            movieIds = query.getResultList();
            movieIds.forEach(System.out::println);

            // Exclude movies already rated by user. and ORDER BY and LIMIT
            jpql = """
                    SELECT m.id FROM Movie m WHERE m.id IN :movieIds AND m.id NOT IN
                            (SELECT a.movie.id FROM AccountMovieRating a WHERE a.account.id =:accountId)
                    ORDER BY m.voteAverage DESC NULLS LAST LIMIT:
                    limit """;
            query = em.createQuery(jpql, Integer.class);
            query.setParameter("movieIds", movieIds);
            query.setParameter("accountId", accountId);
            query.setParameter("limit", limit);
            movieIds = query.getResultList();

            jpql = "SELECT NEW dat.dto.MovieOverviewDto(m) FROM Movie m WHERE m.id IN :movieIds ORDER BY m.voteAverage DESC NULLS LAST";
            TypedQuery<MovieOverviewDto> newQuery = em.createQuery(jpql, MovieOverviewDto.class);
            newQuery.setParameter("movieIds", movieIds);
            List<MovieOverviewDto> recommendations = newQuery.getResultList();

            return recommendations;

        }
    }

}