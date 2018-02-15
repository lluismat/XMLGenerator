/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlgenerator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author lluis
 */
public class XMLGenerator {

      public static void main(String argv[]) {
        try {
            
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver).newInstance();
        System.out.println("Driver " + driver + " Registrado correctamente");

        //Abrir la conexion con la Base de Datos
        System.out.println("Conectando con la Base de datos...");
        String jdbcUrl = "jdbc:mysql://localhost:9000/proyecto4";

        Connection conn = DriverManager.getConnection(jdbcUrl, "grupo4", "dasl4");

        Statement stmt = conn.createStatement();
            
        ResultSet rs = stmt.executeQuery("SELECT * FROM provincia");
           
            
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        //Elemento raíz
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("lista");
        doc.appendChild(rootElement);
        
        //Provincia
        while(rs.next()){
            int idProvincia = rs.getInt("id");
            Element provinciaNode = doc.createElement("provincia");
            rootElement.appendChild(provinciaNode);
            //Se agrega un atributo al nodo elemento y su valor
            Attr attr = doc.createAttribute("id");
            attr.setValue(String.valueOf(idProvincia));
            provinciaNode.setAttributeNode(attr);

            Element nombreNode = doc.createElement("nombre");
            nombreNode.setTextContent(rs.getString("nombre"));
            provinciaNode.appendChild(nombreNode);

            Element poblacionesNode = doc.createElement("localidades");
            provinciaNode.appendChild(poblacionesNode);

            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT * FROM poblacion where provincia_id="+idProvincia);
            
            while(rs2.next()){
                int intPoblacion = rs2.getInt("codigo");
                String nombrePoblacion = rs2.getString("nombre");
                Element poblacionNode = doc.createElement("localidad");
                Attr attr2 = doc.createAttribute("id");
                attr2.setValue(String.valueOf(intPoblacion));
                poblacionNode.setAttributeNode(attr2);
                poblacionNode.setTextContent(nombrePoblacion);
                poblacionesNode.appendChild(poblacionNode);
            }
            
            rs2.close();
            
        }
        
        rs.close();

        //Se escribe el contenido del XML en un archivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("ProvinciasYPoblaciones.xml"));
        transformer.transform(source, result);
            
        } catch (ParserConfigurationException | TransformerException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
