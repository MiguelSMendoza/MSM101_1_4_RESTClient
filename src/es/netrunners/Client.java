package es.netrunners;

public class Client {

	private int ID;
	private String Name;
	private String Surname;
	private int Age;

	public Client() {
		setID(0);
		setName("");
		setAge(0);
		setSurname("");
	}

	public Client(String name, String surname, int age) {
		setID(0);
		setName(name);
		setAge(age);
		setSurname(surname);
	}

	public Client(int ID, String name, String surname, int age) {
		setID(ID);
		setName(name);
		setAge(age);
		setSurname(surname);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return Surname;
	}

	/**
	 * @param surname
	 *            the surname to set
	 */
	public void setSurname(String surname) {
		Surname = surname;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return Age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		Age = age;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

}
