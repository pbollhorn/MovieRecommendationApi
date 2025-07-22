package dat.mappers;

import dat.config.ConnectionPool;
import dat.dto.MovieOverviewDto;
import dat.utils.PropertyReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

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


    public static MovieOverviewDto getMovieOverviewDto(int movieId) {

        String sql = "SELECT id, title, originalLanguage, releaseDate, score, posterPath, rating FROM movie WHERE id = ?";

        return null;

    }


}
