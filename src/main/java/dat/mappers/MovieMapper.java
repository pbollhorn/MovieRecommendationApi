package dat.mappers;

import dat.config.ConnectionPool;
import dat.dto.MovieDetailsDto;
import dat.dto.MovieOverviewDto;
import dat.utils.PropertyReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class MovieMapper {

    private static final String DB_USERNAME = PropertyReader.getPropertyValue("DB_USERNAME");
    private static final String DB_PASSWORD = PropertyReader.getPropertyValue("DB_PASSWORD");
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB_NAME = PropertyReader.getPropertyValue("DB_NAME");
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(DB_USERNAME, DB_PASSWORD, URL, DB_NAME);


    public static Set<Integer> getAllMovieIds() {

        Set<Integer> movieIds = new HashSet<>();

        String sql = "SELECT id FROM movie";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                movieIds.add(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movieIds;

    }


//    public static MovieOverviewDto getMovieOverviewDto(int movieId) {
//
//        MovieOverviewDto movieOverviewDto = null;
//
//        String sql = "SELECT id, title, originallanguage, releasedate, score, posterpath, rating FROM movie WHERE id = ?";
//
//        try (Connection connection = connectionPool.getConnection();
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                int id = rs.getInt("id");
//                String title = rs.getString("title");
//                String originalLanguage = rs.getString("originallanguage");

    /// /                LocalDate releaseDate = rs.getDate("releaseDate");
//                Double score = rs.getDouble("score");
//                String posterPath = rs.getString("posterpath");
//                Boolean rating = rs.getBoolean("rating");
//                return new MovieOverviewDto(id, title, originalLanguage, null, score, posterPath, null, null, rating);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//
//    }
    public static MovieDetailsDto getMovieDetailsDto(int movieId) {

        MovieDetailsDto movieDetailsDto = null;

        String sql = "SELECT id, title, originaltitle, originallanguage, releasedate, voteaverage, backdroppath, overview, runtime FROM movie WHERE id = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String originalTitle = rs.getString("originaltitle");
                String originalLanguage = rs.getString("originallanguage");
                LocalDate releaseDate = rs.getDate("releasedate").toLocalDate();
                Double voteAverage = rs.getDouble("voteaverage");
                String backdropPath = rs.getString("backdroppath");
                String overview = rs.getString("overview");
                Integer runtime = rs.getInt("runtime");
                return new MovieDetailsDto(id, title, originalTitle, originalLanguage, releaseDate, voteAverage, backdropPath, overview, runtime, null, null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }


}
