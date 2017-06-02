package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SingleNewsArticleData {
    private int newsID;
    private String newsType;
    private String newsSlug;
    private String newsUrl;
    private String newsTitle;
    private String newsContent;
    private String newsExcerpt;
    private String newsModifiedTime;
    private String newsThumbnail;


    public SingleNewsArticleData(int _newsID, String _newsType, String _newsSlug, String _newsUrl, String _newsTitle, String _newsContent, String _newsExcerpt, String _newsModifiedTime, String _newsThumbnail) {
        this.newsID = _newsID;
        this.newsType = _newsType;
        this.newsSlug = _newsSlug;
        this.newsUrl = _newsUrl;
        this.newsTitle = _newsTitle;
        this.newsContent = _newsContent;
        this.newsExcerpt = _newsExcerpt;
        this.newsModifiedTime = _newsModifiedTime;
        this.newsThumbnail = _newsThumbnail;
    }

    public int get_newsID() {
        return newsID;
    }

    public String get_newsType() {
        return newsType;
    }

    public String get_newsSlug() {
        return newsSlug;
    }

    public String get_newsUrl() {
        return newsUrl;
    }

    public String get_newsTitle() {
        return newsTitle;
    }

    public String get_newsContent() {
        return newsContent;
    }

    public String get_newsExcerpt() {
        return newsExcerpt;
    }

    public String get_newsModifiedTime() {
        return newsModifiedTime;
    }

    public String get_newsThumbnail() {
        return newsThumbnail;
    }

    public void set_newsID(int newsID) {
        this.newsID = newsID;
    }

    public void set_newsType(String newsType) {
        this.newsType = newsType;
    }

    public void set_newsSlug(String newsSlug) {
        this.newsSlug = newsSlug;
    }

    public void set_newsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public void set_newsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void set_newsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public void set_newsExcerpt(String newsExcerpt) {
        this.newsExcerpt = newsExcerpt;
    }

    public void set_newsModifiedTime(String newsModifiedTime) {
        this.newsModifiedTime = newsModifiedTime;
    }

    public void set_newsThumbnail(String newsThumbnail) {
        this.newsThumbnail = newsThumbnail;
    }
}
