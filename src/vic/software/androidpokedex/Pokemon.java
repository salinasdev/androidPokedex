package vic.software.androidpokedex;

public class Pokemon {
	
	private long id;
	private String nombre;
	private double altura;
	private double peso;
	private int ratio;
	private String descri;
	public Pokemon() {
		super();
	}
	public long getId() {
		return id;
	}
	public void setId(long l) {
		this.id = l;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public double getAltura() {
		return altura;
	}
	public void setAltura(double altura) {
		this.altura = altura;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	
	
}
