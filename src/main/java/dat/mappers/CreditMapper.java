package dat.mappers;

import dat.config.ConnectionPool;
import dat.dto.CreditDto;
import dat.dto.MovieDetailsDto;
import dat.utils.PropertyReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class CreditMapper {

    private static final String DB_USERNAME = PropertyReader.getPropertyValue("DB_USERNAME");
    private static final String DB_PASSWORD = PropertyReader.getPropertyValue("DB_PASSWORD");
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB_NAME = PropertyReader.getPropertyValue("DB_NAME");
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(DB_USERNAME, DB_PASSWORD, URL, DB_NAME);

    public static List<CreditDto> getCreditsForMovie(int movieId) {

        List<CreditDto> creditDtos = new LinkedList<>();

        String sql = "SELECT id, job, character FROM credit JOIN movie ON WHERE id = ?";

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
