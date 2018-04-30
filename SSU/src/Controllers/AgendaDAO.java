/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;
import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import Classes.Agenda;
import Classes.Beneficiario;
import java.util.Calendar;
/**
 *
 * @author Miguel
 */
public class AgendaDAO extends DataBaseConnector {
    

    
    
    
    public static LinkedList<Agenda> getAgendados(Date fechaConsulta, String nombreMedico){
        LinkedList agendados = new LinkedList();
        String ID_Medico = getIDMedico(nombreMedico);
        String fechaHoy ="2018" + "-0"+(fechaConsulta.getMonth()+1) +"-"+fechaConsulta.getDate() ;
        Calendar today = Calendar.getInstance();
        today.setTime(fechaConsulta) ;
        today.add(3, 1);
        
        String tomorrow=" "+today.get(1) +"-0" + (today.get(2)+1)+ "-"  + today.get(3);
        
        System.out.println("Fecha de consulta:" + fechaHoy +  " " + fechaConsulta.toString()  +  " mañana:" + tomorrow + " "+today.getTime()  );
        String query="SELECT * FROM agenda WHERE ID_MEDICO=?  AND Fecha_Agendada =? ;";
        
      //  java.sql.Date(fechaConsulta.getYear(), fechaConsulta.getMonth(), fechaConsulta.getDate());
        
        Connection con = null;
        PreparedStatement pst;
        PreparedStatement pst2;
        ResultSet rs=null;
        ResultSet rs2 =null;
        
        try{
           con=DriverManager.getConnection( connection, username,password );
           pst =  con.prepareStatement(query);
           pst.setString(1, ID_Medico);
           pst.setString(2, fechaHoy);
           rs=pst.executeQuery();
        
           while(rs.next()){
               System.out.println("Agenda Result" + rs.getString(1) + " " + rs.getString(2) +  " "  + rs.getDate(3) + " " + rs.getInt(4) + " turno:" + rs.getInt(6)   );
               String query2 = "SELECT *FROM beneficiarios where ID_Beneficiario=?" ;
               pst2=con.prepareStatement(query2);
               pst2.setInt(1, Integer.parseInt(rs.getString(2)));
               rs2=pst2.executeQuery();
               Date atencion = new Date(rs.getDate(3).getTime() );
               while(rs2.next()){
                   System.out.println("Beneficiario agendado: " +rs2.getString("Primer_Nombre") + " " + rs2.getString("Primer_Apellido")    );
                    Beneficiario ben = new Beneficiario(rs2.getString("Ciudad")   , rs2.getString("Direccion"), rs2.getDate("Fecha_Nacimiento"),rs2.getString("Genero").charAt(0) , rs2.getString("Primer_Nombre"), rs2.getString("Primer_Apellido"), rs2.getString("Segundo_Nombre"), rs2.getString("Segundo_Apellido"), fechaConsulta, "Titular",rs2.getString("ID_Beneficiario"));
        
                   agendados.add(new Agenda( ben, rs.getInt(4),atencion,rs.getInt(6) )  );        
               }
           }
        }catch (SQLException ex){
            System.out.println("Catch from getAgendados" + ex.getMessage());
            
        }finally{
           try{
               if(con!=null)
                   con.close();
           }catch(SQLException exe){
               System.out.println("Connection couldnot close: " + exe.getMessage());
           }
           try{
               if(rs!=null){
                   rs.close();
               }
           }catch(SQLException exe2){
               System.out.println("Controllers.AgendaDAO.getAgendados() resultSet couldnot close" + exe2.getMessage());
           }
            
        }
        
        
        return agendados;
    }
    

    public static String getIDMedico(String nombreMedico){
        String campos[] = nombreMedico.split(" ");
        String nombre =campos[0];
        String apellidoPaterno = campos[1];
//        String apellidoMaterno = campos[2];
        String result="";
 
        String statement= "SELECT * FROM medicos WHERE Primer_Nombre=? and Apellido_Paterno=? ;";
        String query = "SELECT * FROM medicos" ;
        Connection con = null;
        PreparedStatement pst;  
        ResultSet rs=null;
        
        try{
           con=DriverManager.getConnection( connection, username,password );
           pst=con.prepareStatement(statement);
           pst.setString(1, nombre);
           pst.setString(2, apellidoPaterno);
           rs =pst.executeQuery();
           
           
            while(rs.next()){
                System.out.println("Result : "+rs.getString(1) );//+ " " + rs.getString(2) + " " + rs.getNString(3) );
                result = rs.getString("ID_Medico");
            }
            
        }catch(SQLException ex ){
            System.out.println("Controllers.AgendaDAO.getIDMedico()" + ex.getMessage()); 
        }finally{
            
            try{
               if(con!=null)
                   con.close();
           }catch(SQLException exe){
               System.out.println("Connection couldnot close: " + exe.getMessage());
           }
            
           try{
               if(rs!=null){
                   rs.close();
               }
           }catch(SQLException exe2){
               System.out.println("Controllers.AgendaDAO.getAgendados() resultSet couldnot close" + exe2.getMessage());
           }
            
            
        }
        
        return result;
    }
    
    
    public void agendarPaciente(Beneficiario ben,Date fecha,int turno ){
        
        Connection con = null;
        PreparedStatement pst=null;
        String query = "INSERT INTO agenda "
                +" (`ID_Agenda`, `ID_Beneficiario`, `Fecha_Agendada`, `Numero_Consulta`, `ID_Medico, Turno`)   "
                + " values( ?,?,?,?,?,? );";
        try{
            con=DriverManager.getConnection( connection, username,password );
            pst=con.prepareStatement(query);
            pst.setString(1, "");
            pst.setString(2, ben.getID());
            pst.setString(3, fecha.toString() );
            pst.setInt(4, 0);
            pst.setString(5,"");
            pst.setInt(6,turno);
            
            pst.executeQuery();
        }catch(SQLException ex){
            System.out.println("Agendar pacientes has failed:  "+ ex.getMessage());
        }finally{
               try{
               if(con!=null)
                   con.close();
           }catch(SQLException exe){
               System.out.println("Connection couldnot close: " + exe.getMessage());
           }
           try{
               if( pst!=null){
                   pst.close();
               }
           }catch(SQLException exe2){
               System.out.println("Controllers.AgendaDAO.getAgendados() resultSet couldnot close" + exe2.getMessage());
           }
        }
        
    }
    
    
    
    public void agendarOrdenDeLaboratorio(Beneficiario ben,Date fecha,String detalle   ){
        Connection con = null;
        PreparedStatement pst=null;  
       
        String query="insert into orden_de_laboratorio  "+
                "(ID_Beneficiario, ID_Laboratorio,ID_Orden_laboratorio,Fecha_Agendada,Detalle )"+
                "values(?,?,?,?,?   );";
               
        try{
            con=DriverManager.getConnection( connection, username,password );
            pst=con.prepareStatement(query);
            pst.setString(1, ben.getID() );
            pst.setString(2,"");
            pst.setString(3,new java.sql.Timestamp(fecha.getTime()).toString()  );
            pst.setDate(4, new java.sql.Date(fecha.getTime()) );
            pst.setString(5, detalle);
            pst.executeQuery();
            
        }catch(SQLException ex){
            System.out.println("Agendar pacientes has failed:  "+ ex.getMessage());
        }finally{
               try{
               if(con!=null)
                   con.close();
           }catch(SQLException exe){
               System.out.println("Connection couldnot close: " + exe.getMessage());
           }
            
           try{
               if(pst!=null){
                   pst.close();
               }
           }catch(SQLException exe2){
               System.out.println("Controllers.AgendaDAO.getAgendados() resultSet couldnot close" + exe2.getMessage());
           }
            
            
            
        }
        
        
    }
    
    
    
    
    
    
    
}