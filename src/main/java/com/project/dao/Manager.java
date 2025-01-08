package com.project.dao;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import com.project.domain.Autor;
import com.project.domain.Biblioteca;
import com.project.domain.Exemplar;
import com.project.domain.Llibre;
import com.project.domain.Persona;
import com.project.domain.Prestec;

public class Manager {
    private static SessionFactory factory;

    /**
     * Crea la SessionFactory per defecte
     */
    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Registrem totes les classes que tenen anotacions JPA
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("No s'ha pogut crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Crea la SessionFactory amb un fitxer de propietats específic
     */
    public static void createSessionFactory(String propertiesFileName) {
        try {
            Configuration configuration = new Configuration();
            
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            Properties properties = new Properties();
            try (InputStream input = Manager.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
                if (input == null) {
                    throw new IOException("No s'ha trobat " + propertiesFileName);
                }
                properties.load(input);
            }

            configuration.addProperties(properties);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Error creant la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // General

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }


    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where){
        Session session = factory.openSession();
        Transaction tx = null;
        Collection<?> result = null;
        try {
            tx = session.beginTransaction();
            if (where.length() == 0) {
                result = session.createQuery("FROM " + clazz.getName(), clazz).list();
            } else {
                result = session.createQuery("FROM " + clazz.getName() + " WHERE " + where, clazz).list();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }


    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection){
        StringBuilder txt = new StringBuilder();
        for (Object obj : collection) {
            T cObj = clazz.cast(obj);
            txt.append("\n").append(cObj.toString());
        }
        if (txt.length() > 0 && txt.charAt(0) == '\n') {
            txt.deleteCharAt(0);  // Elimina el primer salto de línea si existe
        }
        return txt.toString();
    }

    /**
     * Cierra la SessionFactory.
     */
    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }

    public static String formatMultipleResult(List<Object[]> result) {
        StringBuilder formattedResult = new StringBuilder();
        for (Object[] row : result) {
            for (Object obj : row) {
                formattedResult.append(obj.toString()).append(" | ");
            }
            // Remove the last " | "
            if (formattedResult.length() > 0) {
                formattedResult.setLength(formattedResult.length() - 3);
            }
            formattedResult.append("\n");
        }
        return formattedResult.toString();
    }

    // Autor

    public static Autor addAutor(String string) {
        Session session = factory.openSession();
        Transaction tx = null;
        Autor result = null;
        try {
            tx = session.beginTransaction();
            result = new Autor(string);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    //Biblioteca

    public static Biblioteca addBiblioteca(String nom, String ciutat, String adreça, String telefon, String email) {
        Session session = factory.openSession();
        Transaction tx = null;
        Biblioteca result = null;
        try {
            tx = session.beginTransaction();
            result = new Biblioteca(nom, ciutat, adreça, telefon, email);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public static List<Object[]> findLlibresAmbBiblioteques() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Object[]> result = null;
        try {
            tx = session.beginTransaction();
            
            result = session.createQuery(
                "SELECT l.titol, b.nom " +
                "FROM Llibre l " +
                "JOIN l.exemplars e " +
                "JOIN e.biblioteca b ",
                Object[].class
            ).list();
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    

    //Exemplar

    public static Exemplar addExemplar(String codiBarres, Llibre llibre, Biblioteca biblioteca) {
        Session session = factory.openSession();
        Transaction tx = null;
        Exemplar result = null;
        try {
            tx = session.beginTransaction();
            result = new Exemplar(codiBarres,llibre,biblioteca);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    //Llibre

    public static Llibre addLlibre(String isbn, String titol, String editorial, int anyPublicacio) {
        Session session = factory.openSession();
        Transaction tx = null;
        Llibre result = null;
        try {
            tx = session.beginTransaction();
            result = new Llibre(isbn,titol,editorial,anyPublicacio);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public static void updateAutor(Long autorId, String autorNom, Set<Llibre> llibres) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            Autor autor = session.get(Autor.class, autorId);
            autor.setNom(autorNom);
            autor.setLlibres(llibres);

            session.merge(autor);
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static List<Llibre> findLlibresAmbAutors() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Llibre> result = null;
        try {
            tx = session.beginTransaction();
            
            result = session.createQuery("FROM Llibre l LEFT JOIN FETCH l.autors", Llibre.class).list();
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    

    

    //Persona

    public static Persona addPersona(String dni, String nom, String telefon, String email) {
        Session session = factory.openSession();
        Transaction tx = null;
        Persona result = null;
        try {
            tx = session.beginTransaction();
            result = new Persona(dni, nom, telefon, email);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    //Prestec

    public static Prestec addPrestec(Exemplar exemplar, Persona persona, LocalDate avui, LocalDate dataRetornPrevista) {
        Session session = factory.openSession();
        Transaction tx = null;
        Prestec result = null;
        try {
            tx = session.beginTransaction();
            result = new Prestec(exemplar, persona, avui, dataRetornPrevista);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            result = null;
        } finally {
            session.close();
        }
        return result;
    }

    public static void registrarRetornPrestec(long prestecId, LocalDate dataRetornReal) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            Prestec prestec = session.get(Prestec.class, prestecId);
            prestec.setDataRetornReal(dataRetornReal);

            session.merge(prestec);
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static List<Object[]> findLlibresEnPrestec() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Object[]> result = null;
        try {
            tx = session.beginTransaction();
            
            result = session.createQuery(
                "SELECT l.titol, p.nom " +
                "FROM Llibre l " +
                "JOIN l.exemplars e " +
                "JOIN e.historialPrestecs pr " +
                "JOIN pr.persona p " +
                "WHERE pr.dataRetornReal IS NULL",
                Object[].class
            ).list();

            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    

    
}
