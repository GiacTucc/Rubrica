package dev.rubrica.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

final class ToolbarButtons {
  private ToolbarButtons() {
  }

  static JButton createButton(String text, String iconName) {
    ImageIcon icon = new ImageIcon(Objects.requireNonNull(
        ToolbarButtons.class.getResource("/icons/" + iconName + ".png"),
        "Icona non trovata: " + iconName));
    JButton button = new JButton(text, icon);
    button.setFont(button.getFont().deriveFont(Font.BOLD));
    button.setMargin(new Insets(8, 12, 8, 12));
    button.setIconTextGap(8);
    return button;
  }

  static JToolBar createToolbar(JButton... buttons) {
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    for (int i = 0; i < buttons.length; i++) {
      if (i > 0) { // spazio tra i pulsanti, ma non prima del primo.
        toolbar.addSeparator(new Dimension(6, 1));
      }
      toolbar.add(buttons[i]);
    }
    return toolbar;
  }
}
