package uapi.rx;

import uapi.KernelException;
import uapi.helper.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * The ToMapOperator will collect all of element and put into a Map
 * The previously operator must generate a value which is instance of Pair
 */
class ToMapOperator<KT, VT> extends TerminatedOperator<Map<KT, VT>> {

    ToMapOperator(Operator<Pair<KT, VT>> previously) {
        super(previously);
    }

    @Override
    Map<KT, VT> getItem() throws NoItemException {
        Map<KT, VT> items = new HashMap<>();
        while (hasItem()) {
            try {
                Pair<KT, VT> item = (Pair<KT, VT>) getPreviously().getItem();
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
