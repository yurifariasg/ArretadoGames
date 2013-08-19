package com.arretadogames.pilot.loading;

public class LoadableGLObject {
	
	private int id;
	private LoadableType type;
	private int glId;
	private Object data;
	
	public LoadableGLObject(int id, LoadableType type) {
		this(id, type, null);
	}

	public LoadableGLObject(int id, LoadableType type, Object data) {
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	public Object getData() {
		return data;
	}
	
	public int getId() {
		return id;
	}

	public LoadableType getType() {
		return type;
	}
	
	public void setGLId(int glId) {
		this.glId = glId;
	}
	
	public int getGlId() {
		return glId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LoadableGLObject))
			return false;
		
		return ((LoadableGLObject) o).getId() == getId();
	}
	
	@Override
	public int hashCode() {
		return getId() ^ type.ordinal();
	}
}
