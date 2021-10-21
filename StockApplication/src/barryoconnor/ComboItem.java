package barryoconnor;

/** Helper class to enable the use of ID and String values in a combobox
 *
 * @author Barry O'Connor
 */
public class ComboItem {

    private String id;
    private String name;

    public ComboItem(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public ComboItem(ComboItem[] itemArray) {
        for (ComboItem currItem : itemArray){
            this.id = id;
            this.name = name;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.name;
    }
}
