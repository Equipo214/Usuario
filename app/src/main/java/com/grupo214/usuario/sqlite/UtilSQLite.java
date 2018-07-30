package com.grupo214.usuario.sqlite;


/**
 * Clase beta para guardar contenido de la app. quizas muera como ella.
 * @author  Daniel Boullon
 */
public class UtilSQLite {

    final static public String CAMPO_ID = "id";
    final static public String CAMPOS_LINEA = "lineas";
    final static public String CAMPO_RAMAL = "ramal";
    final static public String CAMPO_CHECKED = "checked";
    final static public String TABLA_LINEA = "linea";

    final static public String CREARTABLALINEA = "CREATE TABLE "+ TABLA_LINEA +" " +
            "("+CAMPO_ID+" INTEGER, "+CAMPOS_LINEA+" TEXT,"+CAMPO_RAMAL+"TEXT,"+CAMPO_CHECKED+"INTEGER)";

    final static public String DROPTABLALINEA = "DROP TABLE IF EXISTS linea";


}
