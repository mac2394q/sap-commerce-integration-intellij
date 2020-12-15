package com.intellij.idea.plugin.hybris.toolwindow;

import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils;
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons;
import com.intellij.idea.plugin.hybris.settings.HybrisDeveloperSpecificProjectSettingsComponent;
import com.intellij.idea.plugin.hybris.settings.HybrisDeveloperSpecificProjectSettingsListener;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent;
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AddEditDeleteListPanel;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ListUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBEmptyBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HybrisToolWindow implements ToolWindowFactory, DumbAware {

    private ToolWindow myToolWindow;
    private Project myProject;
    private JPanel myToolWindowContent;
    private JPanel connectionPanel;
    private MyListPanel myListPanel;
    private ListCellRenderer myListCellRenderer = null;


    @Override
    public void createToolWindowContent(
        @NotNull final Project project, @NotNull final ToolWindow toolWindow
    ) {
        myToolWindow = toolWindow;
        myProject = project;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
        project.getMessageBus().connect(project).subscribe(
            HybrisDeveloperSpecificProjectSettingsListener.TOPIC,
            new HybrisDeveloperSpecificProjectSettingsListener(){
                @Override
                public void hacConnectionSettingsChanged() {
                    loadSettings();
                }
            }
        );
        loadSettings();
    }

    private void loadSettings() {
        final HybrisDeveloperSpecificProjectSettingsComponent instance = HybrisDeveloperSpecificProjectSettingsComponent
            .getInstance(myProject);
        List<HybrisRemoteConnectionSettings> connectionList = instance.getState().getRemoteConnectionSettingsList();
        myListPanel.setInitialList(connectionList);
    }


    @Override
    public boolean shouldBeAvailable(@NotNull final Project project) {
        return HybrisProjectSettingsComponent.getInstance(project).getState().isHybrisProject();
    }

    private void createUIComponents() {
        myListPanel = new MyListPanel(HybrisI18NBundleUtils.message("hybris.toolwindow.remote.label"), new ArrayList<>());
        connectionPanel = myListPanel;
    }

    class MyListPanel extends AddEditDeleteListPanel<HybrisRemoteConnectionSettings> {

        public MyListPanel(
            final String title,
            final List<HybrisRemoteConnectionSettings> initialList
        ) {
            super(title, initialList);
            myList.getModel().addListDataListener(new ListDataListener() {

                @Override
                public void intervalAdded(final ListDataEvent e) {
                    saveSettings();
                }

                @Override
                public void intervalRemoved(final ListDataEvent e) {
                    saveSettings();
                }

                @Override
                public void contentsChanged(final ListDataEvent e) {
                    saveSettings();
                }
            });
        }

        @Nullable
        @Override
        protected HybrisRemoteConnectionSettings editSelectedItem(final HybrisRemoteConnectionSettings item) {
            boolean ok = false;
            if (item.getType() == HybrisRemoteConnectionSettings.Type.SOLR) {
                ok = new SolrConnectionDialog(myProject, myListPanel, item).showAndGet();
                if (ok) {
                    myProject.getMessageBus().syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrConnectionSettingsChanged();
                }
            } else if (item.getType() == null || item.getType() == HybrisRemoteConnectionSettings.Type.Hybris) {
                ok = new RemoteConnectionDialog(myProject, myListPanel, item).showAndGet();
            }
            return ok ? item : null;
        }

        @Nullable
        @Override
        protected HybrisRemoteConnectionSettings findItemToAdd() {
            return null;
        }

        protected void showConfigTypePopup(AnActionButton button) {
            List<HybrisRemoteConnectionSettings.Type> list = Arrays.asList(HybrisRemoteConnectionSettings.Type.values());
            IPopupChooserBuilder<HybrisRemoteConnectionSettings.Type> builder = JBPopupFactory.getInstance().createPopupChooserBuilder(list);
            builder.setRenderer(getListCellRenderer());
            builder.setItemChosenCallback(it->{
                boolean ok = false;
                HybrisRemoteConnectionSettings item = null;
                if (it == HybrisRemoteConnectionSettings.Type.SOLR) {
                    item = HybrisDeveloperSpecificProjectSettingsComponent.getInstance(myProject).getDefaultSolrRemoteConnectionSettings(myProject);
                    ok = new SolrConnectionDialog(myProject, myListPanel, item).showAndGet();
                    if (ok) {
                        myProject.getMessageBus().syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrConnectionSettingsChanged();
                    }
                } else if (it == HybrisRemoteConnectionSettings.Type.Hybris) {
                    item = HybrisDeveloperSpecificProjectSettingsComponent.getInstance(myProject).getDefaultHybrisRemoteConnectionSettings(myProject);
                    ok = new RemoteConnectionDialog(myProject, myListPanel, item).showAndGet();
                }
                if (ok) {
                    addElement(item);
                    saveSettings();
                }
            });
             JBPopup popup = builder.createPopup();
             popup.showUnderneathOf(button.getContextComponent());
        }

        @Override
        protected ListCellRenderer getListCellRenderer(){
            if (myListCellRenderer == null) {
                myListCellRenderer = new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                        final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus
                    ) {
                        final Component comp = super.getListCellRendererComponent(
                            list,
                            value.toString(),
                            index,
                            isSelected,
                            cellHasFocus
                        );
                        ((JComponent)comp).setBorder(new JBEmptyBorder(5));
                        HybrisRemoteConnectionSettings.Type type = null;
                        if (value instanceof HybrisRemoteConnectionSettings.Type) {
                            type = (HybrisRemoteConnectionSettings.Type) value;
                        }
                        if (value instanceof HybrisRemoteConnectionSettings) {
                            type = ((HybrisRemoteConnectionSettings) value).getType();
                        }
                        if (type == HybrisRemoteConnectionSettings.Type.Hybris) {
                            setIcon(HybrisIcons.HYBRIS_ICON);
                        }
                        if (type == HybrisRemoteConnectionSettings.Type.SOLR) {
                            setIcon(HybrisIcons.Console.SOLR);
                        }
                        return comp;
                    }
                };
            }
            return myListCellRenderer;
        }

        public void setInitialList(final List<HybrisRemoteConnectionSettings> remoteConnectionSettingsList) {
            myListModel.clear();
            remoteConnectionSettingsList.forEach(this::addElement);
        }

        protected void customizeDecorator(ToolbarDecorator decorator) {
            decorator.setAddAction(this::showConfigTypePopup);
            super.customizeDecorator(decorator);
            decorator.setMoveUpAction(button -> ListUtil.moveSelectedItemsUp(myList));
            decorator.setMoveDownAction(button -> ListUtil.moveSelectedItemsDown(myList));
        }

        public List<HybrisRemoteConnectionSettings> getData() {
            List<HybrisRemoteConnectionSettings> remoteConnectionSettingsList = new ArrayList<>();
            for (int index=0; index<myList.getModel().getSize(); index++) {
                remoteConnectionSettingsList.add(myList.getModel().getElementAt(index));
            }
            return remoteConnectionSettingsList;
        }
    }

    public void saveSettings() {
        HybrisDeveloperSpecificProjectSettingsComponent.getInstance(myProject).getState().setRemoteConnectionSettingsList(myListPanel.getData());
    }

}
