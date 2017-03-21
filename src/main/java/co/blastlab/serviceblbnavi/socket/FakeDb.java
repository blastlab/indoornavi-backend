package co.blastlab.serviceblbnavi.socket;

import java.util.HashMap;
import java.util.Map;

public class FakeDb {
	private Map<Integer, Anchor> db = new HashMap<>();

	FakeDb() {
		db.put(32768, new Anchor(32768, 0, 0));
		db.put(32769, new Anchor(32769, 1000, 0));
		db.put(32770, new Anchor(32770, 0, -1000));
		db.put(65301, new Anchor(65301, 0, 0));
		db.put(65308, new Anchor(65308, 785, 0));
		db.put(65312, new Anchor(65312, 485, 397));
//		db.put(4, new Anchor(4, 19, 5));;
//		db.put(5, new Anchor(5, 8, 7));
	}

	public Anchor findBy(Integer id) {
		return db.get(id);
	}
}
