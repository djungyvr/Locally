package com.example.djung.locally.AWS;

/**
 * Created by David Jung on 03/11/16.
 */
public class ItemToDisplay {
    // Text for display
    private String labelText;
    private String dataText;
    private String messageText;

    // Display text colors
    private int labelColor;
    private int dataColor;
    private int messageColor;

    // Data box background
    private int dataBackground;

    // Data box drawable
    private String dataDrawable;

    // Constructor
    protected ItemToDisplay(String labelText, String dataText, String messageText,
                            int labelColor, int dataColor, int messageColor,
                            int dataBackground, String dataDrawable) {
        this.labelText = labelText;
        this.dataText = dataText;
        this.messageText = messageText;
        this.labelColor = labelColor;
        this.dataColor = dataColor;
        this.messageColor = messageColor;
        this.dataBackground = dataBackground;
        this.dataDrawable = dataDrawable;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public int getDataColor() {
        return dataColor;
    }

    public void setDataColor(int dataColor) {
        this.dataColor = dataColor;
    }

    public int getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    public int getDataBackground() {
        return dataBackground;
    }

    public void setDataBackground(int dataBackground) {
        this.dataBackground = dataBackground;
    }

    public String getDataDrawable() {
        return dataDrawable;
    }

    public void setDataDrawable(String dataDrawable) {
        this.dataDrawable = dataDrawable;
    }

    @Override
    public String toString() {
        return "ItemToDisplay{" +
                "labelText='" + labelText + '\'' +
                ", dataText='" + dataText + '\'' +
                ", messageText='" + messageText + '\'' +
                ", labelColor=" + labelColor +
                ", dataColor=" + dataColor +
                ", messageColor=" + messageColor +
                ", dataBackground=" + dataBackground +
                ", dataDrawable='" + dataDrawable + '\'' +
                '}';
    }
}
