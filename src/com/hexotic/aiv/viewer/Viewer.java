package com.hexotic.aiv.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javaxt.io.Image;

import org.jdesktop.swingx.border.DropShadowBorder;

public class Viewer extends JFrame {

	private File imageFile;
	private Image image = null;
	private Image original = null;
	private Viewer mainWindow = this;
	private int posX = 0;
	private int posY = 0;
	private int screenHeight = 0;
	private ViewerPanel viewer;
	private Pin pin = new Pin();
	private Timer timer;
	private boolean cropMode = false;
	private boolean showInfo = false;

	public Viewer(File img, int pX, int pY) {
		imageFile = img;
		image = new Image(imageFile);
		original = image.copy();
		setTitle(img.getName());
		setUndecorated(true);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(image.getImage());
		viewer = new ViewerPanel();

		this.setContentPane(viewer);
		this.setBackground(new Color(0, 255, 0, 0));
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new Dimension(image.getWidth(), image.getHeight()));
		screenHeight = dim.height;
		posX = pX;
		posY = pY;
		this.setLocation(posX, posY);
		this.setVisible(true);

		this.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				if (!pin.isPinned()) {
					if (timer != null) {
						timer.stop();
					}
					mainWindow.dispose();
				}
			}
		});

		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_C:
					cropMode = !cropMode;
					break;
				case KeyEvent.VK_ENTER:
					if (cropMode) {
						viewer.saveCrop();
					}
					break;
				case KeyEvent.VK_COMMA:
					viewer.rotate(270);
					break;
				case KeyEvent.VK_PERIOD:
					viewer.rotate(90);
					break;
				case KeyEvent.VK_S:
					if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
						viewer.saveImage();
					}
					break;
				case KeyEvent.VK_MINUS:
					viewer.saturate(1);
					break;
				case KeyEvent.VK_0:
					viewer.saturate(0.0);
					break;
				case KeyEvent.VK_1:
					viewer.saturate(0.1);
					break;
				case KeyEvent.VK_2:
					viewer.saturate(0.2);
					break;
				case KeyEvent.VK_3:
					viewer.saturate(0.3);
					break;
				case KeyEvent.VK_4:
					viewer.saturate(0.4);
					break;
				case KeyEvent.VK_5:
					viewer.saturate(0.5);
					break;
				case KeyEvent.VK_6:
					viewer.saturate(0.6);
					break;
				case KeyEvent.VK_7:
					viewer.saturate(0.7);
					break;
				case KeyEvent.VK_8:
					viewer.saturate(0.8);
					break;
				case KeyEvent.VK_9:
					viewer.saturate(0.9);
					break;
				case KeyEvent.VK_BACK_QUOTE:
					togglePin();
					break;
				case KeyEvent.VK_I:
					// viewer.zoom(100);
					// peel.peel();
					showInfo = !showInfo;
					break;
				case KeyEvent.VK_Z:
					if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
						image = original.copy();
						viewer.zoom(100);
					}
					break;
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
					break;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});
	}

	public ViewerPanel getRenderer() {
		return viewer;
	}

	public void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setLocation(dim.width / 2 - image.getWidth() / 2, dim.height / 2 - image.getHeight() / 2);
	}

	public void togglePin() {
		pin.toggle();
	}

	public class ViewerPanel extends JPanel {

		private int zoom = 100;
		private int[] crop = { 0, 0, 0, 0 };

		public ViewerPanel() {
			this.setLayout(new BorderLayout());
			this.add(headerPanel(), BorderLayout.NORTH);
			this.add(footerPanel(), BorderLayout.SOUTH);
			applyShadow();
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					posX = e.getX();
					posY = e.getY();
					crop[0] = posX;
					crop[1] = posY;
				}
			});

			this.addMouseMotionListener(new MouseAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (cropMode) {
						crop[2] = e.getX() - posX;
						crop[3] = e.getY() - posY;
					} else {
						mainWindow.setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
					}
				}
			});

			this.addMouseWheelListener(new MouseWheelListener() {

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (!cropMode) {
						int rotation = e.getWheelRotation();
						zoom(zoom + (rotation * 2));
					}
				}

			});
			updater();
		}

		public void updater() {
			timer = new Timer(50, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
			});
			timer.start();
		}

		private void applyShadow() {
			DropShadowBorder shadow = new DropShadowBorder();
			shadow.setShadowColor(Color.BLACK);
			shadow.setShowLeftShadow(true);
			shadow.setShowRightShadow(true);
			shadow.setShowBottomShadow(true);
			shadow.setShowTopShadow(true);
			this.setBorder(shadow);
		}

		private JPanel headerPanel() {
			JPanel footer = new JPanel();
			footer.setOpaque(false);
			footer.setLayout(new BorderLayout());
			// peel = new InfoPeel(image);
			// footer.add(peel, BorderLayout.WEST);
			return footer;
		}

		private JPanel footerPanel() {
			JPanel footer = new JPanel();
			footer.setOpaque(false);
			footer.setLayout(new BorderLayout());
			footer.add(pin, BorderLayout.WEST);
			return footer;
		}

		public void refresh() {
			if (!cropMode) {
				for (int i = 0; i < crop.length; i++) {
					crop[i] = 0;
				}
			}
			mainWindow.revalidate();
			mainWindow.repaint();
		}

		public void rotate(int rotate) {
			if (rotate > 359) {
				rotate = 0;
			}
			if (rotate < 0) {
				rotate = 359;
			}
			image.rotate(rotate);
			zoom(zoom);
		}

		public void saveCrop() {
			image.resize(viewer.getWidth(), viewer.getHeight());
			image.crop(crop[0], crop[1], crop[2], crop[3]);
			cropMode = false;
			center();
			zoom(100);
		}

		public void saveResize() {
			image.resize(viewer.getWidth(), viewer.getHeight());
			zoom(100);
		}

		public void saturate(double saturate) {
			image.desaturate(saturate);
		}

		public void saveImage() {
			image.saveAs(imageFile);
		}

		/**
		 * Image zoom level Image can be zoomed from 10-100%
		 * 
		 * @param zoom
		 *            Zoom level
		 */
		public void zoom(int zoom) {
			if (zoom > 10 && ((int) (image.getHeight() * (zoom / 100.0)) < screenHeight) || zoom <= 100) {
				this.zoom = zoom;
				mainWindow.setSize(new Dimension((int) (image.getWidth() * (this.zoom / 100.0)), (int) (image.getHeight() * (this.zoom / 100.0))));
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(image.getImage(), 5, 5, getWidth() - 10, getHeight() - 10, null);

			if (showInfo) {
				g2.setColor(new Color(0, 0, 0, 150));
				g2.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
				g2.setColor(Color.WHITE);
				// Draw Horizontal Line
				g2.drawLine(10, 15, getWidth() - 10, 15);
				g2.drawLine(10, 10, 10, 20);
				g2.drawLine(getWidth() - 10, 10, getWidth() - 10, 20);

				// Draw Vertical Line
				g2.drawLine(getWidth() - 20, 30, getWidth() - 20, getHeight() - 20);
				g2.drawLine(getWidth() - 25, 30, getWidth() - 15, 30);
				g2.drawLine(getWidth() - 25, getHeight() - 20, getWidth() - 15, getHeight() - 20);

				// Draw Width Text
				Font font = new Font("Arial", Font.BOLD, 10);
				g2.setFont(font);
				FontMetrics metrics = g2.getFontMetrics(font);
				String widthText = original.getWidth() + "px";
				if (zoom != 100) {
					widthText = (getWidth() - 10) + "px (Actual: "+widthText+")";
				}

				g2.drawString(widthText, getWidth() / 2 - metrics.stringWidth(widthText) / 2, 25);

			}

			if (cropMode) {
				g2.setColor(new Color(0, 0, 0, 150));
				g2.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
				g2.setColor(new Color(0, 0, 0, 75));
				g2.fillRect(crop[0], crop[1], crop[2], crop[3]);
				g2.setColor(new Color(0, 255, 0, 75));
				g2.drawRect(crop[0], crop[1], crop[2], crop[3]);
			}
		}

	}
}
