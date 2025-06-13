package com.batch.Services.NotificationService.TrayIconNotifier;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseEvent;
import java.net.URL;

@Service
public class TrayIconManager {
    private String toolTip = null;
    private ImageIcon imageicon = null;
    private Image image = null;
    private TrayIcon trayIcon = null;
    private PopupMenu popup = null;
    private SystemTray tray = null;
    private URL resource = null;

    private void addIconToTray() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Toolkit.getDefaultToolkit();
                    if (SystemTray.isSupported()) {
                        TrayIconManager.this.imageicon = new ImageIcon(TrayIconManager.this.resource, "Icon");
                        TrayIconManager.this.image = TrayIconManager.this.imageicon.getImage();
                        TrayIconManager.this.trayIcon = new TrayIcon(TrayIconManager.this.image);
                        TrayIconManager.this.popup = new PopupMenu();
                        TrayIconManager.this.tray = SystemTray.getSystemTray();
                        TrayIconManager.this.tray.add(TrayIconManager.this.trayIcon);
                        TrayIconManager.this.trayIcon.setPopupMenu(TrayIconManager.this.popup);
                        TrayIconManager.this.trayIcon.setToolTip(TrayIconManager.this.toolTip);
                    }
                } catch (AWTException var2) {
                    System.out.println("Unable to init system tray");
                }

            }
        });
    }

    public void addExitMenuItem(final EventOnIconMouseClick callback) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener((event) -> {
                    TrayIconManager.this.tray.remove(TrayIconManager.this.trayIcon);
                    callback.execute();
                });
                TrayIconManager.this.popup.addSeparator();
                TrayIconManager.this.popup.add(exitItem);
            }
        });
    }

    public void addMenuItem(final String name, final EventOnIconMouseClick callback) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MenuItem exitItem = new MenuItem(name);
                exitItem.addActionListener((event) -> callback.execute());
                TrayIconManager.this.popup.add(exitItem);
            }
        });
    }

    public void addEventToIconOnMouseAction(final EventOnIconMouseClick callback) {
        this.trayIcon.addMouseListener(new MouseInputListener() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    callback.execute();
                }

            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }

            public void mouseDragged(MouseEvent me) {
            }

            public void mouseMoved(MouseEvent me) {
            }
        });
    }

    public void desplayMessage(final String Header, final String Content) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TrayIconManager.this.trayIcon.displayMessage(Header, Content, MessageType.NONE);
            }
        });
    }

    public void removeIcon() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TrayIconManager.this.tray.remove(TrayIconManager.this.trayIcon);
            }
        });
    }

    @PostConstruct
    private void atPostConstruct() {
        this.resource = TrayIconManager.class.getResource("icon");
        this.toolTip = this.toolTip;
        this.addIconToTray();
    }
}
