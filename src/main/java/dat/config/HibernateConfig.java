package dat.config;

import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import dat.entities.*;
import dat.enums.Roles;
import dat.utils.PropertyReader;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    public static void setTest(Boolean test) {
        isTest = test;
    }

    public static Boolean getTest() {
        return isTest;
    }

    public static EntityManagerFactory getEntityManagerFactory(String hbm2ddlAutoProperty) {
        if (emf == null)
            emf = createEMF(getTest(), hbm2ddlAutoProperty);
        return emf;
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            setTest(true);
            emfTest = createEMF(getTest(), "create-drop");  // No DB needed for test
        }
        return emfTest;
    }

    // TODO: IMPORTANT: Add Entity classes here for them to be registered with Hibernate
    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Person.class);
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Credit.class);
        configuration.addAnnotatedClass(MovieGenre.class);
        configuration.addAnnotatedClass(Collection.class);

        configuration.addAnnotatedClass(Account.class);
        configuration.addAnnotatedClass(Roles.class);

        configuration.addAnnotatedClass(AccountMovieRating.class);
    }

    private static EntityManagerFactory createEMF(boolean forTest, String hbm2ddlAutoProperty) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            // Set the properties
            setBaseProperties(props, hbm2ddlAutoProperty);
            if (forTest) {
                props = setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                props = setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            return sf.unwrap(EntityManagerFactory.class);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Properties setBaseProperties(Properties props, String hbm2ddlAutoProperty) {
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", hbm2ddlAutoProperty);  // set to "update" when in production
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
        return props;
    }

    private static Properties setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props) {
        String DBName = PropertyReader.getPropertyValue("DB_NAME");
        String DB_USERNAME = PropertyReader.getPropertyValue("DB_USERNAME");
        String DB_PASSWORD = PropertyReader.getPropertyValue("DB_PASSWORD");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", DB_USERNAME);
        props.put("hibernate.connection.password", DB_PASSWORD);
        return props;
    }

    private static Properties setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:16.2:///test_db");
        props.put("hibernate.archive.autodetection", "hbm,class");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        return props;
    }
}
