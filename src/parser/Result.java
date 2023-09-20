package src.parser;


public class Result {
	
	Object obj;
	int index;

	public Result(Object obj, int index) {
		this.index = index;
		this.obj = obj;
	}

	Object getObject() {
		return obj;
	}

	int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "ParserResult [obj=" + obj + ", index=" + index + "]";
	}

}
