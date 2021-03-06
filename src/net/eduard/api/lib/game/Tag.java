package net.eduard.api.lib.game;

import net.eduard.api.lib.modules.Copyable;
/**
 * Representa a Importancia no servidor, Prefixo e Suffixo do Jogador
 * @author Eduard
 * @version 2.0
 * @since EduardAPI 1.0
 * 
 */
public class Tag {

	private String name,prefix, suffix ;

	private int rank;

	public Tag(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public Tag(String prefix, String suffix, String name) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.name = name;
	}

	public Tag() {

	}

	public Tag(String prefix, String suffix, String name, int rank) {
		setPrefix(prefix);
		setSuffix(suffix);
		setName(name);
		setRank(rank);
		
	}

	public Tag copy() {
		return Copyable.copyObject(this);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;

	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}



	@Override
	public String toString() {
		return "Tag [prefix=" + prefix + ", suffix=" + suffix + ", name=" + name + ", rank=" + rank + "]";
	}

}
