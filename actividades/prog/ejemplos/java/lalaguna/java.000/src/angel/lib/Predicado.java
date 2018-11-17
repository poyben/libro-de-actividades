
package angel.lib;

import  angel.utl.*;
import  java.io.*;
import 	java.lang.*;


/**
 * Toma como entrada un <b>String</b> que contiene un predicado y crea una
 * representaci�n interna en forma de arbol para facilitar su tratamiento.
 * 
 * Restricciones:
 * a) Cada predicado se encierra entre par�ntesis.
 * b) Los t�rminos son secuencias alfanum�ricas.
 * c) El separador de t�rminos es el espacio y los par�ntesis.
 * d) Si un t�rmino comienza por ? se entiendo como una variable.
 *
 * @author David Vargas Ruiz
 * @version 0.1.0
 */

public class Predicado extends Object
{
   public static final int   ERROR=-1, NUEVO=0, OK=1, NUM_MAX_FILAS=10, NUM_MAX_COLUMNAS=5;
   public static final int   TERM_VACIO=-2, TERM_CONSTANTE=2, TERM_PREDICADO=3, TERM_VARIABLE=4;
   
   private String[][] arbol;
   private int[][]    arbolAux;
   private int[]      arbolNumTerminos;
   private int        cod_estado;
   private String     des_estado;
   private int        numeroPredicados;
   private int        numeroTerminos;
   private int	      numeroVariables;
   private int        profundidad;
   private String     des_predicado;
   
	
   //================================
   // Constructor de la clase
   //================================ 
   public Predicado(String p_str)
   {
      cod_estado=NUEVO; des_estado=new String("NUEVO");
      arbol=new String[NUM_MAX_FILAS][NUM_MAX_COLUMNAS]; arbolAux=new int[NUM_MAX_FILAS][NUM_MAX_COLUMNAS];
      arbolNumTerminos=new int[NUM_MAX_FILAS];
      profundidad=0; numeroTerminos=0; numeroPredicados=0; numeroVariables=0;
      des_predicado=new String(p_str);
      try  {   extraerArbol(p_str);}
      catch (Exception e)
      {
         des_estado=new String ("ERR 01:"+e); cod_estado=ERROR; 
         System.err.println(des_estado);
      }
   }
	

   private void extraerArbol(String p_texto) 
   {
      SepararTerminos st = new SepararTerminos(p_texto);
      String	str;

      int parentesis=0, lineaLibre=-1, lineaActual=-1;
      int[] auxVieneDe   = new int[NUM_MAX_FILAS];          int[] auxTermLibre = new int [NUM_MAX_FILAS];
      for(int i=0; i<NUM_MAX_FILAS;i++) auxVieneDe[i]=-1;   for(int i=0; i<NUM_MAX_FILAS;i++) auxTermLibre[i]=-1;
      for(int i=0; i<NUM_MAX_FILAS;i++) arbolNumTerminos[i]=0;
  
      //Bucle que recorre toda el texto del predicado
      while(st.fin()==false)
      {	
         str = new String(st.siguiente());
    	 if (str.equals("("))
    	 {  //Se ha encontrado un par�ntesis abierto
    	    if (numeroTerminos==0 && parentesis==0) 
	    {  //Estamos en el primer par�ntesis del �rbol
    	       lineaActual=0;       lineaLibre=1;
    	       auxVieneDe[0]=-1;    auxTermLibre[0]=0;
    	       arbolAux[0][0]=-1;   numeroPredicados++;
    	       arbol[1][0]="/";     arbolAux[1][0]=-1;
    	    }
    	    else if (parentesis>0) 
   	    {  //No es el primer par�ntesis
    	       arbol[lineaActual][auxTermLibre[lineaActual]]=null;
    	       arbolAux[lineaActual][auxTermLibre[lineaActual]]=lineaLibre;
    	       auxTermLibre[lineaActual]++;
    			
    	       auxVieneDe[lineaLibre]=lineaActual;  auxTermLibre[lineaLibre]=0;  arbolAux[lineaLibre][0]=-1;
	       lineaActual=lineaLibre;   lineaLibre++;  numeroPredicados++;
	       arbol[lineaLibre][0]="/";		arbolAux[lineaLibre][0]=-1;
            }
	    else //Error en los par�ntesis
	    { des_estado = new String ("ERR 02: Error en parentesis"); cod_estado=ERROR;break;	}
	    parentesis++;
    	    if (parentesis>profundidad+1) {profundidad=parentesis-1;}
    	 }
    	 else if (str.equals(")"))
    	 { //Se ha encontrado un par�ntesis cerrado
    	    arbol[lineaActual][auxTermLibre[lineaActual]]=null;
    	    arbolAux[lineaActual][auxTermLibre[lineaActual]]=-1;
    	    arbolNumTerminos[lineaActual]=auxTermLibre[lineaActual];
    	    auxTermLibre[lineaActual]=1000;
    	    lineaActual=auxVieneDe[lineaActual];
            parentesis--;
    	    if (parentesis < 0) 
    	    { des_estado=new String("ERR 03: Error parentesis despu�s del t�rmino "+numeroTerminos); cod_estado=ERROR;}
    	 }
    	 else
    	 {	
    	    //Guardar nuevo t�rmino en el �rbol
    	    //if (str.startsWith("_")) str = new String("#"+str);
    	    numeroTerminos++;
    	    arbol[lineaActual][auxTermLibre[lineaActual]]=new String(str);
    	    arbolAux[lineaActual][auxTermLibre[lineaActual]]=0;
    	    auxTermLibre[lineaActual]++;
    	    if (str.startsWith("?")) numeroVariables++;
         }
      }
    
      if (parentesis>0)
      { cod_estado=ERROR; des_estado=new String ("ERR 04: Falta cerrar "+parentesis+" par�ntesis.");}
      else if (parentesis<0)
      { cod_estado=ERROR; des_estado=new String ("ERR 05: Falta abrir "+parentesis+" par�ntesis."); }
      if (cod_estado==NUEVO) {cod_estado=OK; des_estado=new String("OK");}
   }

   
   //================================
   public int contarTermino(String t)
   {
      int c=0;
      for(int i=0;i<NUM_MAX_FILAS;i++)
      {
         if (arbolAux[i][0]<0) break;
	 for(int j=0;j<NUM_MAX_COLUMNAS;j++)
	 {
	    if ((arbolAux[i][j]==0) && arbol[i][j].equals(t)) c++;
	    else if (arbolAux[i][j]<0) break;
	 }
      }
      return c;
   }

   //================================
   public boolean estaCorrecto()	            { if (cod_estado==OK) return true; else return false;}
   
   public int     leeCodEstado()	            { return cod_estado;}
   public String  leeDesEstado()	            { return des_estado;}
   public int     leeNumeroPredicados()             { return numeroPredicados; }
   public int     leeNumeroTerminos()               { return numeroTerminos; }
   public int     leeNumeroTerminosPredicado(int p) 
   { 
      if (p>=0 && p<NUM_MAX_FILAS && p<numeroPredicados) return arbolNumTerminos[p]; 
      return 0;
   }
		   
   public int     leeNumeroVariables()              { return numeroVariables; }
   public int     leePunteroPredicado(int p, int t)	
   {	
      if (p>=0 && p<NUM_MAX_FILAS && t>=0 && t<NUM_MAX_COLUMNAS)
      { if (arbolAux[p][t]>0) return arbolAux[p][t];   }
      return -1;
   }

   public int     leeProfundidad()                  { return profundidad; }
   public String  leeDesPredicado()                 { return des_predicado;}   
   
   public String  leeTermino(int p, int t)	
   {	
      if (p>=0 && p<NUM_MAX_FILAS && p<=numeroPredicados && t>=0 && t<NUM_MAX_COLUMNAS && t<=arbolNumTerminos[p])
      {
         if (arbolAux[p][t]==0) return arbol[p][t]; //Se devuelve un t�rmino
         else if (arbolAux[p][t]>0) return (" "+arbolAux[p][t]); 
	 //Se devuelve un puntero a predicado con formato espacio+n�mero
	 //Ning�n t�rmino va a tener espacios delante excepto estos enlaces a predicados
      }
      return "";	//Esta posici�n est� vac�a
   }
   
   public int     leeTipoTermino(int p, int t)	
   {	
      if (p>=0 && p<NUM_MAX_FILAS && t>=0 && t<NUM_MAX_COLUMNAS)
      {
         if (arbolAux[p][t]<0)       return TERM_VACIO; 
	 else if (arbolAux[p][t]>0)  return TERM_PREDICADO; 
         else if (arbol[p][t].startsWith("?")) return TERM_VARIABLE;
	 return TERM_CONSTANTE;
      }
      return TERM_VACIO;	//Esta posici�n est� vac�a
   }
} //Fin de Predicado.java
 