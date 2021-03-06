package com.jpodder.ui.swt.feeds;
import com.jpodder.data.feeds.IXPersonalFeed;

import de.kupzog.ktable.KTableSortComparator;
import de.kupzog.ktable.KTableSortedModel;

public class KFeedComparator extends KTableSortComparator {

    public KFeedComparator(KTableSortedModel model, int columnIndex, int direction) {
        super(model, columnIndex, direction);
    }

    public int doCompare(Object o1, Object o2, int row1, int row2) {
        int titleResult = 0;
        if (o1 instanceof IXPersonalFeed && o2 instanceof IXPersonalFeed) {
            // compare the title.
            String title1 = ((IXPersonalFeed) o1).getPersonalTitle();
            String title2 = ((IXPersonalFeed) o2).getPersonalTitle();
            if (title1 != null && title2 != null)
                titleResult = title1.compareToIgnoreCase(title2);
            if (titleResult == 0) {
            }
        }
        return titleResult;
    }
}
