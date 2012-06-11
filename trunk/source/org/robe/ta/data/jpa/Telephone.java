package org.robe.ta.data.jpa;
import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TELEPHONES", schema = "APP")
public class Telephone implements Serializable
{
	private int id;
	private String name;
	private String description;
	private BigInteger telephone;
	
	@Column(name="NAME")
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	@Column(name="DESCRIPTION")
	public String getDescription() 
	{
		return description;
	}
	
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
	@Column(name="TELEPHONE")
	public BigInteger getTelephone() 
	{
		return telephone;
	}
	
	public void setTelephone(BigInteger telephone) 
	{
		this.telephone = telephone;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	@Override
	public String toString() 
	{
		return "Telephone (id=" + id + ",name=" + name + ",description=" + description + ",telephone=" + telephone + ")";
	}
}