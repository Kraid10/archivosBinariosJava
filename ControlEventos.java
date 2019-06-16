package proyecto3;
import java.io.*;
import java.util.Scanner;

public class ControlEventos {
	static Scanner teclado = new Scanner(System.in);
	static final String path = "C:\\Users\\kevin\\Documents\\Ejercicios Java\\Proyecto3 aleatorios\\src\\proyecto3\\archivo.dat";//Archivo principal
	static final String rtindx = "C:\\Users\\kevin\\Documents\\Ejercicios Java\\Proyecto3 aleatorios\\src\\proyecto3\\indice.dat";//Archivo indice
	static RandomAccessFile arch = null;//Para manipular el archivo principal de acceso aleatorio

	public static void main(String[] args) {
		System.out.println("\t Registro de Eventos \n");
		menuPrincipal();

	}
	
	private static void menuPrincipal() {
		int opc;
		System.out.println("\t *** Menú Principal ***");
		System.out.println("Selecciona una opción");
		System.out.println("1 - Nuevo Evento");
		System.out.println("2 - Buscar Registro");
		System.out.println("3 - Listar Registros");
		System.out.println("4 - Modificar Registro");
		System.out.println("0 - Salir");
		opc = teclado.nextInt();
		
		switch (opc){
		case 0:
			System.exit(0);
			break;
		case 1:
			ingresarRegistro();
		break;
		case 2:
			buscarRegistro();
		break;
		case 3:
			listarDatos();
		break;
		case 4:
			modificar();
		break;
		default:
			System.out.println("Opción no válida");
			menuPrincipal();
		break;
		}
	}

	private static void ingresarRegistro(){
		String fecha,hora,artista,opc,ampm,hra;//Variables que contienen los datos a guardar en el archivo
		int asistentes,pos,verif;
		
		do{
		System.out.println("Nuevo evento");
		System.out.println("Fecha de evento: dd/mm/aaa");//Solicitamos la informacion
		fecha = teclado.next();
		verif = validarFecha(fecha);//Para que no se ingresen fechas repetidas, abajo esta como funciona
		if (verif == 1){
			System.out.println("La fecha seleccionada no está disponible, por favor ingresa una fecha diferente: ");
			ingresarRegistro();
		}
		
		System.out.println("Hora de Inicio: hh:mm");
		hra = teclado.next();//Capturamos los datos
		teclado.nextLine();
		System.out.println("AM / PM");
		ampm = teclado.next();
		hora = hra + ampm;
		teclado.nextLine();
		System.out.println("Artista: ");
		artista = teclado.nextLine();
		teclado.nextLine();
		System.out.println("Cantidad de asistentes");
		asistentes = teclado.nextInt();
		System.out.println("Desea ingresar otro registro?  S/N");
		opc = teclado.next();
		
		try{
			FileOutputStream out = new FileOutputStream(rtindx,true);//Con esto abrimos y manipulamos el indice
			DataOutputStream dout = new DataOutputStream(out);
			arch = new RandomAccessFile(path, "rw");//El archivo de acceso aleatorio se abre en modo lectura y escritura 
			
			pos = (int) arch.length();//Para conocer la posicion en la que debemos escribir dentro del archivo
			arch.seek(pos);//Posiciona el puntero de escritura al final del archivo
			//System.out.println(pos); Solo queria saber si me daba la posicion correcta
			arch.writeUTF(fecha);//Escribe el valor de la variable en el archivo principal
			dout.writeUTF(fecha);//Escribe en el archivo indice
			dout.writeInt(pos);//Guarda, en el indice, la posicion que tomara el registro en el archivo principal
			pos +=12;//Calcula la posicion siguiente a escribir
			arch.seek(pos);//Coloca el puntero en la posicion que corresponde escribir
			arch.writeUTF(hora);
			pos +=9;
			arch.seek(pos);
			arch.writeUTF(artista);
			pos +=50;
			arch.seek(pos);
			arch.writeInt(asistentes);
			dout.close();
		
		}catch(Exception e){
			System.out.println("Ha ocurrido un error");//Por si algo sale mal
			e.printStackTrace();
		}finally{
			try{
				
				if(arch != null){
					arch.close();//Cierra el archivo despues de usarlo
				}
			}catch(Exception f){
				System.out.println("Ha ocurrido un error");
			}
		}
		}while(opc.equalsIgnoreCase("s"));//Evalua si queremos seguir ingresando registros
		menuPrincipal();
	}

	private static void listarDatos(){
		int pos = 0;//Variable para la posicion del puntero de lectura
				
		try{
			arch = new RandomAccessFile(path, "r");//Abre el archivo principal en modo lectura
			
			do{
				arch.seek(pos);//Indica la posicion del puntero al iniciar la lectura
				System.out.println("Fecha de evento:  " + arch.readUTF());
				pos += 12;//Igual que para escribir, mueve el puntero hacia el siguiente dato a leer
				arch.seek(pos);
				System.out.println("Hora del evento: " + arch.readUTF());
				pos += 9;
				arch.seek(pos);
				System.out.println("Artista: " + arch.readUTF());
				pos += 50;
				arch.seek(pos);
				System.out.println("Cantidad de asistentes: " + arch.readInt() + " personas \n");
				pos += 4;
			}while(true);//El proceso se repite mientras hayan datos en el archivo principal
				
						
		}catch(Exception e){//El error que muestra es porque ya no hay datos que leer en el archivo
			System.out.println("\t ***FIN DEL ARCHIVO***");//Para que no muestre el error y se vea mas bnito
			try {
				arch.close();//Siempre hay que cerrar el archivo
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		menuPrincipal();//Regresa al menu
	}
	
	public static int leerIndice(String busca){//Este metodo servira para hacer la busqueda, recibe por parametro la fecha a buscar
		String fecha;//Las usaremos para leer el indice
		int pos = 0, posicion = 0;
		
		try{
					FileInputStream in = new FileInputStream(rtindx);
					DataInputStream din = new DataInputStream(in);//Para manipular el archivo indice
					
					try{
						do{
							fecha = din.readUTF();//Realizara la lectura del archivo indice
							pos = din.readInt();
							if(busca.equals(fecha)){//Si el valor encontrado es igual al valor de busqueda
								posicion = pos;//Se guardara en otra variable el valor de la posicion del puntero
							}
							
						}while(busca != fecha);//La lectura se hara mientras el valor encontrado sea diferente al valor buscado
					}catch(Exception f){
						System.out.println(" ");//Solo para que no mestre el error de fin de archivo
					}finally{
						din.close();//Se cierra el archivo
					}
				}catch(Exception e){
					e.printStackTrace();//Este otro error si lo mostramos
				}
			return posicion;//Al final el metodo devolvera la posicion del registro que estamos buscando

	}
	
	private static void buscarRegistro(){
		String fecha;//Se realizara la busqueda por medio de la fecha
		int pos = 0;
		System.out.println("\t Buscar Registro");
		System.out.println("Fecha que deseas buscar: ");
		fecha = teclado.next();//Capturamos la fecha que vamos a buscar
		pos = leerIndice(fecha);//Con este buscamos la posicion en el indice, arriba dice como funciona
		System.out.println(pos);
		try{
			arch = new RandomAccessFile(path, "r");//Abre el archivo en modo lectura
						
				arch.seek(pos);//Utiliza el valor que buscamos en el indice para iniciar la lectura
				//El mismo proceso de lectura
				System.out.println("Fecha de evento:  " + arch.readUTF());
				pos += 12;
				arch.seek(pos);
				System.out.println("Hora del evento: " + arch.readUTF());
				pos += 9;
				arch.seek(pos);
				System.out.println("Artista: " + arch.readUTF());
				pos += 50;
				arch.seek(pos);
				System.out.println("Cantidad de asistentes: " + arch.readInt() + " personas \n");
				
									
		}catch(Exception e){
			System.out.println("\t ***FIN DEL REGISTRO***");
			try {
				arch.close();
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		menuPrincipal();
	}
	
	private static int validarFecha(String dto){//Comprueba que la fecha que se va a utilizar no ha sido agregada previamente
		//Recibe por parametro la fecha que se va a ingresar
		String fecha;
		int pos,resp = 0;
		try{
				//Se buscara en el indice, es mas facil
				FileInputStream in = new FileInputStream(rtindx); //Manipular el archivo indice
				DataInputStream din = new DataInputStream(in);
				
				try{
					do{
						fecha = din.readUTF();
						pos = din.readInt();
						
						if(dto.equals(fecha)){//Si encuentra el valor de la fecha, le asignara un valor a una variable
							resp = 1;//Este valor solo es de referencia para mi y saber si la fecha ya exixte enel archivo o no
						}
					}while(dto != fecha);
				}catch(Exception f){
					System.out.println(" ");
				}finally{
					din.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		return resp;//Devuelve el valor de referencia

	}
	
	private static void modificar(){
		String fecha, fechanv, hra, hora, ampm, artista;
		int asistentes,pos = 0,posdf;
			
		System.out.println("Fecha que deseas buscar para modificar: ");
		fecha = teclado.next();
		pos = leerIndice(fecha);
		posdf = leerIndice(fecha);
		
		try{
			arch = new RandomAccessFile(path, "r");
			
			System.out.println("Este registro se modificará \n");
			arch.seek(pos);
			System.out.println("Fecha de evento:  " + arch.readUTF());
			pos += 12;
			arch.seek(pos);
			System.out.println("Hora del evento: " + arch.readUTF());
			pos += 9;
			arch.seek(pos);
			System.out.println("Artista: " + arch.readUTF());
			pos += 50;
			arch.seek(pos);
			System.out.println("Cantidad de asistentes: " + arch.readInt() + " personas \n");
			pos += 4;
		}catch(Exception e){
			System.out.println("Ha ocurrido un error");
		}
		
			System.out.println("Ingresa los nuevos datos: \n");
			System.out.println("Hora de Inicio: hh:mm");
			hra = teclado.next();
			teclado.nextLine();
			System.out.println("AM / PM");
			ampm = teclado.next();
			hora = hra + ampm;
			teclado.nextLine();
			System.out.println("Artista: ");
			artista = teclado.nextLine();
			teclado.nextLine();
			System.out.println("Cantidad de asistentes");
			asistentes = teclado.nextInt();
			
			try{
				RandomAccessFile arch = new RandomAccessFile(path, "rw");
				
				posdf += 12;
				arch.seek(posdf);
				arch.writeUTF(hora);
				posdf +=9;
				arch.seek(posdf);
				arch.writeUTF(artista);
				posdf +=50;
				arch.seek(posdf);
				arch.writeInt(asistentes);
				System.out.println("\t ***Se ha modificado el registro***");
				
			}catch(Exception f){
				System.out.println("Ha ocurrido un error");
				f.printStackTrace();
			}finally{
				try{
					if(arch != null){
						arch.close();
					}
				}catch(Exception f){
					System.out.println("Ha ocurrido un error");
				}
			}
				menuPrincipal();
	}

}