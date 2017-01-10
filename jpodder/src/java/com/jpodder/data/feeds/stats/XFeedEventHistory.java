package com.jpodder.data.feeds.stats;

import java.util.*;

import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.XPersonalFeed;
import com.jpodder.util.Messages;
import com.jpodder.util.Util;

/**
 * The feed events are stored in a model, where
 * by each feed has it's own history. An event is a separate object also
 * defined as en enclosed class. The following is defined: - Add a new
 * feed to the list of feeds from which history should be maintained -
 * Remove a feed from the list of feeds from which history should be
 * maintained - Add a history event for a feed. - Get all history events
 * for a feed. - Get the last history event for a feed.
 */
public class XFeedEventHistory {

    public static int FEED_INSTRUCTION_EVENT = 0;

    public static String[] typeDescriptions = { "Instruction event" };

    public ArrayList events;

    public XFeedEventHistory() {
        events = new ArrayList();
    }

    /**
     * Add an event to the history.
     * 
     * @param feed
     * @param eventObject
     */
    public void addEvent(XPersonalFeed feed, Object eventObject) {

        XFeedHistoryEvent event = new XFeedHistoryEvent();
        event.setFeed(feed);
        event.setStamp(System.currentTimeMillis());
        event.setEventObject(eventObject);
        if (eventObject instanceof XFeedInstruction) {
            event.setType(FEED_INSTRUCTION_EVENT);
        } else {
            // Unknown event type.
        }
        events.add(events.size(), event);
    }

    public void addEvent(XFeedHistoryEvent event) {
        events.add(events.size(), event);
    }

    public void removeEvent(XFeedHistoryEvent event) {
        events.remove(event);
    }

    public XFeedHistoryEvent getLatestEvent() {
        XFeedHistoryEvent event = null;

        if (events.size() > 0) {
            event = (XFeedHistoryEvent) events.get(events.size() - 1);
        }
        return event;
    }

    /**
     * An event object.
     */
    public class XFeedHistoryEvent {
        private IXPersonalFeed feed;

        private long stamp;

        private Object eventObject = null;

        private int Type = -1;

        /**
         * @return Returns the feed.
         */
        public IXPersonalFeed getFeed() {
            return feed;
        }

        /**
         * @param feed
         *            The feed to set.
         */
        public void setFeed(IXPersonalFeed feed) {
            this.feed = feed;
        }

        /**
         * @return Returns the stamp.
         */
        public long getStamp() {
            return stamp;
        }

        /**
         * @param stamp
         *            The stamp to set.
         */
        public void setStamp(long stamp) {
            this.stamp = stamp;
        }

        /**
         * @return Returns the type.
         */
        public int getType() {
            return Type;
        }

        /**
         * @param type
         *            The type to set.
         */
        public void setType(int type) {
            Type = type;
        }

        /**
         * @return Returns the eventObject.
         */
        public Object getEventObject() {
            return eventObject;
        }

        /**
         * @param eventObject
         *            The eventObject to set.
         */
        public void setEventObject(Object eventObject) {
            this.eventObject = eventObject;
        }

        /**
         * Get a formated String of the event flags.
         * 
         * @return String
         */
        public String getFormatedFlags() {
            StringBuffer buf = new StringBuffer();
            if (eventObject instanceof XFeedInstruction) {
                XFeedInstruction i = (XFeedInstruction) eventObject;
                if (i.collect)
                    buf.append(" "
                            + Messages
                                    .getString("feedeventhistory.collectFeed"));
                if (i.collectEnclosure)
                    buf.append(" " + Messages
                                    .getString("feedeventhistory.collectEncl"));
                if (i.download)
                    buf.append(" " + Messages
                                            .getString("feedeventhistory.download"));
                if (i.inspect)
                    buf.append(" " + Messages.getString("feedeventhistory.inspect"));
                if (i.mark)
                    buf.append(" " + Messages.getString("feedeventhistory.mark"));
                if (i.store)
                    buf.append(" "
                            + Messages.getString("feedeventhistory.store"));
            }
            return buf.toString();

        }

        public String getDate() {
            StringBuffer buf = new StringBuffer();
            buf.append(Util.formatDate(new Date(stamp)));
            return buf.toString();

        }

        public String toString() {

            StringBuffer buf = new StringBuffer();
            buf.append(Messages.getString("feedeventhistory.event"));
            buf.append(" " + typeDescriptions[Type]);
            return buf.toString();
        }

    }

    /**
     * @return Returns the events.
     */
    public ArrayList getEvents() {
        return events;
    }

    /**
     * @param events
     *            The events to set.
     */
    public void setEvents(ArrayList events) {
        this.events = events;
    }
}