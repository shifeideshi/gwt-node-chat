package com.ywebb.chat.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class Gwt_chat implements EntryPoint {

	public void onModuleLoad() {

		RootPanel root = RootPanel.get("root");
		VerticalPanel rootPanel = new VerticalPanel();
		root.add(rootPanel);

		createLoginDialog();
	}

	void createLoginDialog() {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Welcome to GWT Node Chat");
		dialogBox.setAnimationEnabled(true);

		final Button sendButton = new Button("Enter");
		final TextBox nameField = new TextBox();

		sendButton.addStyleName("sendButton");

		FlexTable table = new FlexTable();

		table.setWidget(0, 0, new HTML("Please choose a nickname"));
		table.setWidget(0, 1, nameField);
		table.setWidget(0, 2, sendButton);
		table.setCellPadding(10);
		dialogBox.setWidget(table);

		class MyHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				if (nameField.getText() != null && nameField.getText().trim().length() > 0) {
					doLogin(nameField.getText());
					dialogBox.hide();
				}
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onClick(null);
				}
			}
		}

		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);

		centerDialog(dialogBox);
	}

	void createChatDialog(final String id) {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.setSize("390px", "300px");
		dialogBox.setText("Chatting..");
		dialogBox.setAnimationEnabled(true);

		final Button sendButton = new Button("Send");
		final TextArea inText = new TextArea();

		sendButton.addStyleName("sendButton");

		FlexTable table = new FlexTable();

		ScrollPanel panel = new ScrollPanel(new HTML("Lorem ipsum dolor sit amet, consectetuer...Lorem ips"
				+ "um dolor si" + " dolor sit amet, consectetuer...Lorem ipsum dolor sit amet, consectetuer..."));

		panel.setSize("100%", "300px");
		table.setWidget(0, 0, panel);
		table.setWidget(1, 0, inText);
		table.setWidget(1, 1, sendButton);
		FlexCellFormatter fc = table.getFlexCellFormatter();
		fc.setColSpan(0, 0, 2);
		table.setCellPadding(10);
		dialogBox.setWidget(table);

		class MyHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				doSendMessage(inText.getText(), id);
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onClick(null);
				}
			}
		}

		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		inText.addKeyUpHandler(handler);

		centerDialog(dialogBox);
	}

	void doLogin(String nick) {
		String url = "/chat/join?_=" + new Date().getTime() + "&nick=" + nick;

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("exception:" + exception);
				}

				public void onResponseReceived(Request request, Response response) {
					Window.alert(response.getStatusCode() + " response '" + response.getText() + "'");
					if (200 == response.getStatusCode()) {
						JSONValue jsonValue = JSONParser.parse(response.getText());
						JSONObject obj = jsonValue.isObject();
						JSONValue id = obj.get("id");
						String nmb = id.toString();
						nmb = nmb.substring(1, nmb.length() - 1);
						createChatDialog(nmb);

					} else {
						// Handle the error. Can get the status text from
						// response.getStatusText()
					}
				}
			});
		} catch (RequestException e) {
		}
	}

	void centerDialog(final DialogBox dialogBox) {
		dialogBox.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int leftStart = (Window.getClientWidth() - offsetWidth) / 2;
				int topEnd = (Window.getClientHeight() - offsetHeight) / 2;
				dialogBox.setPopupPosition(leftStart, topEnd);
			}
		});
	}

	void doLongPoll() {
		// HTTP
	}

	void doSendMessage(String msg, String id) {

		String url = "/chat/send?_=" + new Date().getTime() + "&id=" + id + "&text=" + URL.encode(msg);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("exception:" + exception);
				}

				public void onResponseReceived(Request request, Response response) {
					Window.alert(response.getStatusCode() + "response '" + response.getText() + "'");
					if (200 == response.getStatusCode()) {
						// Process the response in response.getText()
					} else {
						// Handle the error. Can get the status text from
						// response.getStatusText()
					}
				}
			});
		} catch (RequestException e) {
		}
	}

	public static void main(String[] args) {
		Date date = new Date();
		int iTimeStamp = (int) (date.getTime() * 1);
		System.out.println(iTimeStamp);
	}
}
