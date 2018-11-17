/*-----------------------------------------------
   Programa :  WFLinea.java
   Fecha    :  11-06-2003
   Estado   :  Desarrollo
-----------------------------------------------*/
package totem.lib;

import  java.io.*;
import  java.util.*;
import  java.sql.*;

/**
 * Clase que representa cada l�nea de informaci�n asociada al tiquet.
 * 
 * @author David Vargas Ruiz
 * @version 0.7.3
 */
public class WFLinea extends Object
{
   final int OK=0, MODIF=1, NUM_VALORES=20;
   
   private WFTicket	   ticket;
   private WFConcepto      concepto;
   private String	   cod_usuario, des_estado;
   private java.util.Date  fec_ult_mod;
   private int		   cod_linea, cod_estado;
   private Vector	   valor;

   
   //=======================
   //Constructor de la clase
   //=======================
   public WFLinea(WFTicket p_ticket, int p_cod_linea)
   {
      ticket=null;concepto=null;fec_ult_mod=null;cod_usuario=null;cod_linea=-1;
      cod_estado=OK;des_estado=new String("OK");  valor = new Vector(NUM_VALORES);
      
      Statement st1; ResultSet rs1;
      try
      {
	 st1 = p_ticket.leeConnection().createStatement();
	 //Localizar los campos del registro linea
	 /*rs1 = st1.executeQuery("SELECT * FROM JOB_D_TICKET_LINEAS lin , JOB_M_TICKETS tic "
		+" WHERE lin.cod_ticket="+p_ticket.leeCodTicket()
		  +" AND lin.cod_ticket=tic.cod_ticket "
		  +" AND lin.cod_linea="+p_cod_linea+";");*/
	 rs1 = st1.executeQuery("SELECT * FROM JOB_D_TICKET_LINEAS lin "
		+" WHERE lin.cod_ticket="+p_ticket.leeCodTicket()
		  +" AND lin.cod_linea="+p_cod_linea+";");

	 if (rs1.next())
	 { 
	    //Cargar los campos de la tabla TICKET_LINEAS en el objeto java
	    cod_linea=p_cod_linea; ticket=p_ticket;
            concepto=new WFConcepto(p_ticket.leeConnection(),rs1.getInt("COD_CONCEPTO"));
	    fec_ult_mod=rs1.getDate("FEC_ULT_MOD");cod_usuario=rs1.getString("COD_USUARIO");

	    //Cargar los registros de la tabla TICKET_VALORES en el vector java	 
            rs1 = st1.executeQuery("SELECT val.COD_LINEA as COD_LINEA, val.COD_CAMPO as COD_CAMPO "
			    +"FROM JOB_D_LINEA_VALORES val "
			    +"WHERE  val.cod_linea="+p_cod_linea
			    +" ORDER BY val.COD_CAMPO;");
	    
	    for(int i=0;i<NUM_VALORES;i++)
	    {
	       if (rs1.next()) valor.addElement(new WFValor(this,rs1.getInt("COD_CAMPO")));
	       else i=NUM_VALORES;
	    }
	    
	    //valor=rs1.getString("VALOR"));
	    //chk_modificable= rs1.getBoolean("CHK_MODIFICABLE");chk_notnull=rs1.getBoolean("CHK_NOTNULL");
	    
	    //Cargar las variables de la tabla TICKET_HIST_VALORES en el objeto java
	    /*rs1 = st1.executeQuery("SELECT his.* FROM JOB_D_TICKET_HIST_VALORES his "
		  +" WHERE his.cod_ticket="+p_ticket.getCodTicket()
		  +" AND his.cod_campo="+p_cod_campo+" ORDER BY FECHA;");
	    for(int i=0;i<VAL_NUM_HISTORICO;i++)
	    {
	       if (rs1.next()) historico.addElement(new WFHistValor(p_ticket.getCodTicket(),p_cod_campo,
			rs1.getString("VALOR"),rs1.getDate("FECHA"),rs1.getString("COD_USUARIO")));
	       else i=VAL_NUM_HISTORICO;
	    }*/
	 }
	 rs1.close();
	 st1.close();
      }
      catch(Exception e)
      {  System.err.println("Exception WFValor() REG_NOTFOUND: " + e);}
   }

   
   
   //============================================================================
   //M�todos GET
   //============================================================================

   public java.util.Date   leeFecUltMod()     { return fec_ult_mod;}
   public int	           leeCodConcepto()   { return concepto.leeCodConcepto();}
   public int	   	   leeCodLinea()      { return cod_linea;}
   public int		   leeCodTarea()      { return ticket.leeCodTarea();}
   public int		   leeCodTicket()     { return ticket.leeCodTicket();}
   public String	   leeCodUsuario()    { return cod_usuario;}
   public String           leeCodUsuarioSesion() { return ticket.leeCodUsuarioSesion();}
   public Connection	   leeConnection()    { return ticket.leeConnection();}
   public String	   leeDesConcepto()   { return concepto.leeDesConcepto();}
   public int   	   leeNumValores()    { return valor.size();}
   public WFValor	   leeValor(int i)	   
   {
      if (i<valor.size()) return ((WFValor) valor.get(i));
      return null;
   }


   //============================================================================
   //M�todos SET
   //============================================================================
   
   //==========
   //ponValor()
   //==========
   /*public boolean ponValor(String s)
   { 
      if (valor.equals(s)&&cod_estado!=UPDATED) return false;

      Pattern patron = Pattern.compile(campo.getCodMascara());
      Matcher encaja = patron.matcher(s);
      
      if (encaja.matches())
      {  //Chequear que <s> es del tipo adecuado seg�n el tipo de campo
	 valor=new String(s); fec_ult_mod=new java.util.Date(); cod_usuario=new String(ticket.getCodUsuarioSesion());
	 cod_estado=VAL_UPDATED;
	 return true;
      }
      return false;
   }*/

   
   //========
   //commit()
   //========
   public boolean commit()
   { 
      if (cod_estado==MODIF)
      {
	 Statement st1,st2;
	 ResultSet rs1;
	 try
	 {
	    /*st1 = ticket.getConnection().createStatement();
	    st1.executeUpdate("INSERT INTO JOB_D_TICKET_HIST_VALORES (cod_ticket,cod_campo,valor,cod_usuario) VALUES("
		  +ticket.getCodTicket()+","+campo.getCodCampo()+",'"+valor+"','"+cod_usuario+"')");
	    st1.executeUpdate("UPDATE JOB_D_TICKET_VALORES SET valor='"+valor+"', cod_usuario='"+cod_usuario+"' "
		  +" WHERE cod_ticket="+ticket.getCodTicket()
		  +" AND cod_campo="+campo.getCodCampo());
	    st1.close();*/
	    cod_estado=OK;
	    return true;
	 }
	 catch(Exception e)
	 { System.err.println("Exception WFLinea().commit("+cod_linea+"):"+e);}
      }
      return true;
   }
}
//Fin de la clase WFLinea.java