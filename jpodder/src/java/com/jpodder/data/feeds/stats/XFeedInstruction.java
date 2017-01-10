/**
 * Created on Jan 7, 2005
 */
package com.jpodder.data.feeds.stats;


/**
 * A feed instruction class. 
 **/
public class XFeedInstruction {
    
    protected boolean collect = false;

    protected boolean collectEnclosure = false;

    protected boolean inspect = false;

    protected boolean download = false;

    protected boolean store = false;

    protected boolean mark = false;

    protected Object source;
    
    
    /**
     * Constructor;
     * 
     * @param src
     * @param collect
     * @param encl
     * @param mark
     * @param inspect
     * @param download
     * @param store
     */
    public XFeedInstruction(Object src, boolean collect, boolean encl, boolean mark,
            boolean inspect, boolean download, boolean store) {
        this.source = src;
        this.collect = collect;
        this.collectEnclosure = encl;
        this.inspect = inspect;
        this.mark = mark;
        this.download = download;
        this.store = store;
    }
    
    /**
     * @return Returns the source.
     */
    public Object getSource() {
        return source;
    }
    /**
     * @return Returns the collect.
     */
    public boolean isCollect() {
        return collect;
    }
    /**
     * @param collect The collect to set.
     */
    public void setCollect(boolean collect) {
        this.collect = collect;
    }
    /**
     * @return Returns the collectEnclosure.
     */
    public boolean isCollectEnclosure() {
        return collectEnclosure;
    }
    /**
     * @param collectEnclosure The collectEnclosure to set.
     */
    public void setCollectEnclosure(boolean collectEnclosure) {
        this.collectEnclosure = collectEnclosure;
    }
    /**
     * @return Returns the download.
     */
    public boolean isDownload() {
        return download;
    }
    /**
     * @param download The download to set.
     */
    public void setDownload(boolean download) {
        this.download = download;
    }
    /**
     * @return Returns the inspect.
     */
    public boolean isInspect() {
        return inspect;
    }
    /**
     * @param inspect The inspect to set.
     */
    public void setInspect(boolean inspect) {
        this.inspect = inspect;
    }
    /**
     * @return Returns the mark.
     */
    public boolean isMark() {
        return mark;
    }
    /**
     * @param mark The mark to set.
     */
    public void setMark(boolean mark) {
        this.mark = mark;
    }
    /**
     * @return Returns the store.
     */
    public boolean isStore() {
        return store;
    }
    /**
     * @param store The store to set.
     */
    public void setStore(boolean store) {
        this.store = store;
    }
}
