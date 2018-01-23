package com.here.name.website.Civitas.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Charles on 12/23/2017.
 */

public class TextPost implements Parcelable{
    private String text;
    private String date_created;
    private String user_id;
    private String tags;
    private List<Like> likes;
    private List<Comment> comments;

    public TextPost() {

    }

    public TextPost(String caption, String date_created,
                    String image_path, String photo_id,
                    String user_id, String tags, List<Like>
                         likes, List<Comment> comments) {
        this.text = text;
        this.date_created = date_created;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.comments = comments;
    }

    protected TextPost(Parcel in) {
        text = in.readString();
        date_created = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(date_created);
        dest.writeString(user_id);
        dest.writeString(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextPost> CREATOR = new Creator<TextPost>() {
        @Override
        public TextPost createFromParcel(Parcel in) {
            return new TextPost(in);
        }

        @Override
        public TextPost[] newArray(int size) {
            return new TextPost[size];
        }
    };

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static Creator<TextPost> getCREATOR() {
        return CREATOR;
    }

    public String getText() {
        return text;
    }

    public void setCaption(String text) {
        this.text = text;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "text='" + text + '\'' +
                ", date_created='" + date_created + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", likes=" + likes +
                '}';
    }
}
