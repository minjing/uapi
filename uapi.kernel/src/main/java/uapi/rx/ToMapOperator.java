package uapi.rx;

import uapi.KernelException;
import uapi.helper.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * The ToMapOperator will collect all of element and put into a Map
 * The previously operator must generate a value which is instance of Pair
 */
class ToMapOperator<K, V> extends TerminatedOperator<Map<K, V>> {

    ToMapOperator(Operator<Pair<K, V>> previously) {
        super(previously);
    }

    @Override
    Map<K, V> getItem() throws NoItemException {
        Map<K, V> items = new HashMap<>();
        while (hasItem()) {
            try {
                Pair<K, V> item = (Pair<K, V>) getPreviously().getItem();
                if (item == null) {
                    continue;
                }
                items.put(item.getLeftValue(), item.getRightValue());
            } catch (NoItemException ex) {
                // do nothing
            } catch (ClassCastException ex) {
                throw new KernelException("The ToMapOperator requires previously operator generate item is instance of Pair");
            }
        }
        return items;
    }
}
