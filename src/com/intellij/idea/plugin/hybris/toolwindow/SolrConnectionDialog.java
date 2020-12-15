package com.intellij.idea.plugin.hybris.toolwindow;

import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils;
import com.intellij.idea.plugin.hybris.notifications.NotificationUtil;
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings;
import com.intellij.idea.plugin.hybris.tools.remote.http.SolrHttpClient;
import com.intellij.idea.plugin.hybris.toolwindow.document.filter.UnsignedIntegerDocumentFilter;
import com.intellij.idea.plugin.hybris.toolwindow.document.listener.SimpleDocumentListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT;

public class SolrConnectionDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(SolrConnectionDialog.class);

    private JPanel contentPane;
    private JTextField displayNameTextField;
    private JButton testConnectionButton;
    private JTextField solrWebrootTextField;
    private JLabel hacWebrootLabel;
    private JLabel projectUrlPreviewValueLabel;
    private JLabel projectUrlPreviewLabel;
    private JTextField solrPortTextField;
    private JLabel projectPortLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel loginNameLabel;
    private JTextField loginTextField;
    private JTextField solrIpTextField;
    private JLabel projectIpLabel;
    private Project myProject;
    private HybrisRemoteConnectionSettings mySettings;

    public SolrConnectionDialog(
        @Nullable final Project project,
        @Nullable final Component parentComponent,
        @NotNull final HybrisRemoteConnectionSettings settings
    ) {
        super(project, parentComponent, false, PROJECT);
        myProject = project;
        mySettings = settings;

        displayNameTextField.setText(mySettings.getDisplayName());
        solrIpTextField.setText(mySettings.getHostIP());
        solrPortTextField.setText(mySettings.getPort());
        ((PlainDocument) solrPortTextField.getDocument()).setDocumentFilter(new UnsignedIntegerDocumentFilter());

        solrWebrootTextField.setText(mySettings.getSolrWebroot());
        loginTextField.setText(mySettings.getAdminLogin());
        passwordField.setText(mySettings.getAdminPassword());

        final SimpleDocumentListener saveSettingsDocumentListener = new SimpleDocumentListener() {
            @Override
            public void update(final DocumentEvent e) {
                saveSettings();
            }
        };

        saveSettings();
        init();

        displayNameTextField.getDocument().addDocumentListener(saveSettingsDocumentListener);
        solrIpTextField.getDocument().addDocumentListener(saveSettingsDocumentListener);
        solrPortTextField.getDocument().addDocumentListener(saveSettingsDocumentListener);
        solrWebrootTextField.getDocument().addDocumentListener(saveSettingsDocumentListener);
        displayNameTextField.addActionListener(action->saveSettings());
        solrIpTextField.addActionListener(action->saveSettings());
        solrWebrootTextField.addActionListener(action->saveSettings());
        loginTextField.addActionListener(action->saveSettings());
        passwordField.addActionListener(action->saveSettings());
        testConnectionButton.addActionListener(action->testConnection());
    }

    private void testConnection() {
        saveSettings();
        String message;
        NotificationType type;
        try {
            SolrHttpClient.getInstance(myProject).listOfCores(myProject, mySettings);
            message = HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.success", "SOLR" , mySettings.getGeneratedURL());
            type = NotificationType.INFORMATION;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            type = NotificationType.WARNING;
            message = HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.fail", mySettings.getGeneratedURL(), e.getMessage());
        }

        NotificationUtil.NOTIFICATION_GROUP.createNotification(
            HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.title"), message, type, null
        ).notify(myProject);
    }

    private void saveSettings() {
        mySettings.setDisplayName(displayNameTextField.getText());
        mySettings.setType(HybrisRemoteConnectionSettings.Type.SOLR);
        mySettings.setHostIP(solrIpTextField.getText());
        mySettings.setPort(solrPortTextField.getText());
        mySettings.setSolrWebroot(solrWebrootTextField.getText());
        mySettings.setAdminLogin(loginTextField.getText());
        mySettings.setAdminPassword(new String(passwordField.getPassword()));
        final String previewUrl = CommonIdeaService.getInstance().getHostSolrUrl(myProject, mySettings);
        projectUrlPreviewValueLabel.setText(previewUrl);
        mySettings.setGeneratedURL(previewUrl);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
