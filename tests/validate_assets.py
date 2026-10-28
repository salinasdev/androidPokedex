"""Checks requiring only Python 3, not an Android SDK or emulator."""
from pathlib import Path
import sqlite3
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]


class BundledCatalogueTest(unittest.TestCase):
    def setUp(self):
        self.db = sqlite3.connect((ROOT / "assets/DBPOK").as_uri() + "?mode=ro", uri=True)
        self.addCleanup(self.db.close)

    def test_integrity_and_version(self):
        self.assertEqual("ok", self.db.execute("PRAGMA integrity_check").fetchone()[0])
        self.assertEqual(0, self.db.execute("PRAGMA user_version").fetchone()[0])

    def test_complete_catalogue(self):
        for table, key in (("pokemon", "id"), ("detalles", "id_poke")):
            self.assertEqual(list(range(1, 152)), [row[0] for row in
                self.db.execute(f"SELECT {key} FROM {table} ORDER BY {key}")])
        details = self.db.execute("SELECT descripcion, altura, peso, ratio FROM detalles").fetchall()
        self.assertTrue(all(text and height > 0 and weight > 0 and 0 <= ratio <= 255
                            for text, height, weight, ratio in details))

    def test_bound_literal_prefix_queries(self):
        query = "SELECT id FROM pokemon WHERE nombre LIKE ? ESCAPE '\\' ORDER BY id"
        self.assertEqual([(25,)], self.db.execute(query, ("pika%",)).fetchall())
        for text in ("'", "' OR 1=1 --", "%", "_", "\\"):
            prefix = text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%"
            self.assertEqual([], self.db.execute(query, (prefix,)).fetchall())

    def test_sprites_and_cries(self):
        for number in range(1, 152):
            self.assertTrue(list((ROOT / "res").glob(f"drawable*/p{number}.png")), number)
            sound = ROOT / f"res/raw/s{number}.mp3"
            self.assertTrue(sound.is_file(), number)
            self.assertGreater(sound.stat().st_size, 0)

    def test_xml_is_well_formed(self):
        for path in [ROOT / "AndroidManifest.xml", *(ROOT / "res").rglob("*.xml")]:
            with self.subTest(path=path.relative_to(ROOT)):
                ET.parse(path)


if __name__ == "__main__":
    unittest.main(verbosity=2)