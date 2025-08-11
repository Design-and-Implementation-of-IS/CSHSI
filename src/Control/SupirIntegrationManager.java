package Control;

import Entity.Item;
import Entity.ItemCategory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manager for importing external Items from the Supir XML feed.
 * Responsible for parsing, filtering against existing DB records (via InventoryManager),
 * and delegating inserts.
 */
public class SupirIntegrationManager {

    public static final String XML_PATH = "C:/Users/Daniel/Desktop/מאור/מערכות מידע/שנה ב/תכן/שיעורי בית/2/HW2/DentalCare/src/Entity/Items.xml";
    private static final String DATE_PATTERN = "yyyy-MM-dd"; // matches XML date format

    private final InventoryManager inventoryManager;

    public SupirIntegrationManager() { this.inventoryManager = new InventoryManager(); }
    public SupirIntegrationManager(InventoryManager inv) { this.inventoryManager = inv; }

    /** Load and parse all items from XML file. */
    public List<Item> loadAllFromXml() throws Exception {
        File f = new File(XML_PATH);
        if (!f.exists()) throw new IllegalStateException("Supir XML file not found: " + XML_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(f);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getElementsByTagName("Items");
        System.out.println("Found " + nodes.getLength() + " Items nodes in XML");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) n;
            try {
                Item item = buildItemFromElement(e);
                if (item != null) items.add(item);
            } catch (Exception ex) {
                // skip problematic entry, could log
            }
        }
        return items;
    }

    /** Extract serial numbers from list. */
    public List<String> extractSerials(List<Item> list) {
        List<String> res = new ArrayList<>();
        for (Item it : list) res.add(it.getSerialNumber());
        return res;
    }

    /** Filter items that are not yet in DB (by serial). */
    public List<Item> filterMissing(List<Item> xmlItems, Collection<String> existingSerials) {
        Set<String> existing = new HashSet<>(existingSerials);
        List<Item> missing = new ArrayList<>();
        for (Item it : xmlItems) {
            if (!existing.contains(it.getSerialNumber())) missing.add(it);
        }
        return missing;
    }

    /** Import single item (delegates to InventoryManager). */
    public boolean importItem(Item item) {
        if (item == null) return false;
        return inventoryManager.insertItem(item);
    }

    /** Import many items, returning map serial -> success flag. */
    public Map<String, Boolean> importAll(List<Item> items) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Item item : items) {
            boolean ok = importItem(item);
            result.put(item.getSerialNumber(), ok);
        }
        return result;
    }

    /** Build Item entity from XML element. */
    private Item buildItemFromElement(Element e) throws ParseException {
        String serial = text(e, "itemSerialNum");
        if (serial == null || serial.isBlank()) return null;
        String name = text(e, "itemName");
        String desc = text(e, "itemDescription");
        String dateStr = text(e, "expirationDate");
        String categoryRaw = text(e, "category");
        String supplierStr = text(e, "supplierId");

        Date exp = null;
        try { exp = new SimpleDateFormat(DATE_PATTERN).parse(dateStr); } catch (Exception ex) { exp = new Date(); }

        ItemCategory catEnum;
        try { catEnum = ItemCategory.valueOf(categoryRaw); }
        catch (Exception ex) { catEnum = ItemCategory.Tools; }

        int supplierId = 0;
        try { supplierId = Integer.parseInt(supplierStr); } catch (NumberFormatException ignored) { return null; }
        if (supplierId <= 0) return null;

        return new Item(serial, name, desc, exp, catEnum, supplierId);
    }

    private String text(Element e, String tag) {
        NodeList nl = e.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        Node n = nl.item(0);
        return n.getTextContent().trim();
    }
}
