package com.microsoft.office365.test.integration.tests;


import android.util.Log;

import com.microsoft.office365.test.integration.ApplicationContext;
import com.microsoft.office365.test.integration.framework.TestCase;
import com.microsoft.office365.test.integration.framework.TestGroup;
import com.microsoft.office365.test.integration.framework.TestResult;
import com.microsoft.office365.test.integration.framework.TestStatus;
import com.microsoft.outlookservices.Attendee;
import com.microsoft.outlookservices.BodyType;
import com.microsoft.outlookservices.Calendar;
import com.microsoft.outlookservices.CalendarGroup;
import com.microsoft.outlookservices.Contact;
import com.microsoft.outlookservices.EmailAddress;
import com.microsoft.outlookservices.Event;
import com.microsoft.outlookservices.FileAttachment;
import com.microsoft.outlookservices.Folder;
import com.microsoft.outlookservices.Importance;
import com.microsoft.outlookservices.ItemBody;
import com.microsoft.outlookservices.Message;
import com.microsoft.outlookservices.Recipient;
import com.microsoft.outlookservices.odata.OutlookClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ExchangeTests extends TestGroup {

    public ExchangeTests() {
        super("Exchange tests");

        this.addTest(canCreateClient("Can create client", true));
        // Folders
        this.addTest(canRetrieveFolders("Can retrieve folders", true));
        this.addTest(canRetrieveFolderById("Can retrieve folder by id", true));
        this.addTest(canRetrieveFolder("Can Retrieve Folder (overload)", true));
        this.addTest(canCreateFolder("Can create folder", true));
        this.addTest(canDeleteFolder("Can delete folder", true));
        this.addTest(canMoveFolder("Can move folder", true));
        this.addTest(canCopyFolder("Can copy folder", true));
        this.addTest(canUpdateFolder("Can update folder", true));

        //Messages
        this.addTest(canGetMessages("Can get messages", true));
        this.addTest(canGetMessage("Can get messages (overload)", true));
        this.addTest(canCreateMessage("Can create message in drafts", true));
        this.addTest(canCreateMessageAttachment("Can create message with attachment", true));
        this.addTest(canSendMessage("Can send message", true));
        this.addTest(canUpdateMessage("Can update message", true));
        this.addTest(canDeleteMessage("Can delete message", true));
        this.addTest(canMoveMessage("Can move message", true));
        this.addTest(canCopyMessage("Can copy message", true));
        this.addTest(canCreateReplyMessage("Can create reply", true));
        this.addTest(canCreateReplyAllMessage("Can create reply all", true));
        this.addTest(canCreateForwardMessage("Can create forward", true));
        //TODO:reply action
        //TODO:reply all
        //TODO:forward action

        //Calendar groups
        this.addTest(canCreateCalendarGroup("Can create calendar group", true));
        this.addTest(canGetCalendarGroups("Can get calendar groups", true));
        this.addTest(canGetCalendarGroupById("Can get calendar group by id", true));
        this.addTest(canUpdateCalendarGroup("Can update calendar group", true));
        this.addTest(canDeleteCalendarGroup("Can delete calendar group", true));

        // Calendars
        this.addTest(canCreateCalendar("Can create calendar", true));
        this.addTest(canGetCalendars("Can get calendars", true));
        this.addTest(canGetDefaultCalendar("Can get default calendar", true));
        this.addTest(canGetCalendarById("Can get calendar by id", true));
        this.addTest(canUpdateCalendar("Can update calendar", true));
        this.addTest(canDeleteCalendar("Can delete calendar", true));

        //Events
        this.addTest(canGetEvents("Can get events", true));
        this.addTest(canCreateEvents("Can create events", true));
        this.addTest(canUpdateEvents("Can update events", true));
        this.addTest(canDeleteEvents("Can delete events", true));

        //Contacts
        this.addTest(canGetContactsFolder("Can get contacts folder", true));
        this.addTest(canGetContacts("Can get contacts", true));
        this.addTest(canCreateContact("Can create contacts", true));
        this.addTest(canDeleteContact("Can delete contacts", true));
        this.addTest(canUpdateContact("Can update contacts", true));

        //Select, top, filter
        this.addTest(canFilterMessages("Can use filter in messages", true));
        this.addTest(canSelectMessages("Can use select in messages", true));
        this.addTest(canTopMessages("Can use top in messages", true));

    }


    //Messages
    private TestCase canRetrieveFolderById(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder folder = client.getMe().getFolders().getById("Inbox").read().get();
                    if (folder == null || !folder.getDisplayName().equals("Inbox"))
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canRetrieveFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder folder = client.getMe().getFolder("Inbox").read().get();
                    if (folder == null || !folder.getDisplayName().equals("Inbox"))
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    String newFolderName = "TestFolder" + UUID.randomUUID();
                    String parentFolderName = "Inbox";

                    //Create new folder
                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder newFolder = new Folder();
                    newFolder.setDisplayName(newFolderName);
                    Folder addedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .add(newFolder).get();

                    // Assert
                    Folder folder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .getById(addedFolder.getId()).read().get();

                    if (folder == null || !folder.getDisplayName().equals(newFolderName)) {
                        result.setStatus(TestStatus.Failed);
                    }

                    //Cleanup
                    client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .getById(folder.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    String newFolderName = "TestFolder"  + UUID.randomUUID();;
                    String parentFolderName = "Inbox";

                    //Prepare for test
                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder newFolder = new Folder();
                    newFolder.setDisplayName(newFolderName);
                    Folder addedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .add(newFolder).get();

                    // Delete folder
                    client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .getById(addedFolder.getId())
                            .delete().get();

                    // Assert
                    List<Folder> folders = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .read().get();

                    boolean exists = false;
                    for (Folder f : folders) {
                        if (f.getId().equals(addedFolder.getId())) {
                            exists = true;
                        }
                    }
                    if (exists)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canMoveFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    String newFolderName = "TestFolder"  + UUID.randomUUID();;
                    String parentFolderName = "Inbox";
                    String destinationFolderName = "Drafts";

                    //Create new folder
                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder newFolder = new Folder();
                    newFolder.setDisplayName(newFolderName);
                    Folder addedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .add(newFolder).get();

                    //Act
                    client.getMe()
                            .getFolders()
                            .getById(parentFolderName).getChildFolders()
                            .getById(addedFolder.getId())
                            .getOperations().move(destinationFolderName).get();

                    //Assert
                    Folder movedFolder = client.getMe()
                            .getFolders()
                            .getById(destinationFolderName)
                            .getChildFolders()
                            .getById(addedFolder.getId())
                            .read().get();

                    if (movedFolder != null && movedFolder.getDisplayName().equals(newFolderName))
                        result.setStatus(TestStatus.Passed);

                    //Cleanup
                    client.getMe()
                            .getFolders()
                            .getById(destinationFolderName)
                            .getChildFolders()
                            .getById(movedFolder.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCopyFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    String newFolderName = "TestFolder"  + UUID.randomUUID();;
                    String parentFolderName = "Inbox";
                    String destinationFolderName = "Drafts";

                    //Create new folder
                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder newFolder = new Folder();
                    newFolder.setDisplayName(newFolderName);
                    Folder addedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .add(newFolder).get();

                    //Act
                    Folder copiedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName).getChildFolders()
                            .getById(addedFolder.getId())
                            .getOperations().copy(destinationFolderName).get();

                    //Assert
                    Folder folder = client.getMe()
                            .getFolders()
                            .getById(destinationFolderName)
                            .getChildFolders()
                            .getById(copiedFolder.getId())
                            .read().get();

                    if (folder != null && folder.getDisplayName().equals(newFolderName))
                        result.setStatus(TestStatus.Passed);

                    //Cleanup
                    client.getMe()
                            .getFolders()
                            .getById(destinationFolderName)
                            .getChildFolders()
                            .getById(copiedFolder.getId())
                            .delete().get();

                    client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .getById(addedFolder.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }


    private TestCase canUpdateFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    String folderName = "TestFolder"  + UUID.randomUUID();;
                    String updatedFolderName = "UpdatedTestFolder"  + UUID.randomUUID();;
                    String parentFolderName = "Inbox";

                    //Create new folder
                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    Folder newFolder = new Folder();
                    newFolder.setDisplayName(folderName);
                    Folder addedFolder = client.getMe()
                            .getFolders()
                            .getById(parentFolderName)
                            .getChildFolders()
                            .add(newFolder).get();

                    //Act
                    newFolder.setDisplayName(updatedFolderName);
                    client.getMe()
                            .getFolders()
                            .getById(addedFolder.getId())
                            .update(newFolder).get();

                    // Assert
                    Folder folder = client.getMe()
                            .getFolders()
                            .getById(addedFolder.getId()).read().get();

                    if (folder == null || !folder.getDisplayName().equals(updatedFolderName)) {
                        result.setStatus(TestStatus.Failed);
                    }

                    //Cleanup
                    client.getMe()
                            .getFolders()
                            .getById(folder.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    // Messages tests
    private TestCase canGetMessages(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    List<Message> inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(3).read().get();
                    if(inboxMessages.size()== 0)
                    {
                        String mailSubject = "Test get Message";
                        Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                        client.getMe().getOperations().sendMail(message, true).get();
                        Thread.sleep(2000);
                        inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    }

                    if (inboxMessages == null || inboxMessages.size() == 0 || inboxMessages.size() > 3)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    //Test new syntatic collection fetcher overload to avoid calling getByIdXXX methods.
    private TestCase canGetMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    Message message = getSampleMessage("Test message", ApplicationContext.getTestMail(), "");

                    //Act
                    Message createdMessage = client.getMe().getMessages().add(message).get();

                    //Assert
                    Message searchedMessage = client.getMe()
                            .getFolder("Drafts")
                            .getMessage(createdMessage.getId()).read().get();

                    if (searchedMessage == null || !searchedMessage.getSubject().equals("Test message"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getFolder("Drafts")
                            .getMessage(createdMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateClient(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    if (client == null)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canRetrieveFolders(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();
                    List<Folder> folders = client.getMe().getFolders().read().get();
                    if (folders == null || folders.size() == 0)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    Message message = getSampleMessage("Test message", ApplicationContext.getTestMail(), "");

                    //Act
                    Message createdMessage = client.getMe().getMessages().add(message).get();

                    //Assert
                    Message searchedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(createdMessage.getId()).read().get();

                    if (searchedMessage == null || !searchedMessage.getSubject().equals("Test message"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(createdMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateMessageAttachment(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    Message message = getSampleMessage("Test message", ApplicationContext.getTestMail(), "");

                    //Act
                    Message added = client.getMe().getMessages().add(message).get();
                    client.getMe().getMessages()
                            .getById(added.getId())
                            .getAttachments()
                            .add(getFileAttachment()).get();

                    //Assert
                    Message storedMessage = client.getMe().getMessages().getById(added.getId()).read().get();

                    if(!storedMessage.getHasAttachments())
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canSendMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    String mailSubject = "Test Send Message";
                    Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                    //Act
                    client
                            .getMe().getOperations().sendMail(message, true).get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canUpdateMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    String mailSubject = "Test Update Message";
                    Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                    //Act
                    Message addedMessage = client.getMe().getMessages().add(message).get();
                    message.setSubject("New Test Update Message");
                    client
                            .getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(addedMessage.getId())
                            .update(message).get();

                    Thread.sleep(1000);
                    //Assert
                    Message updatedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(addedMessage.getId()).read().get();

                    if (updatedMessage == null || !updatedMessage.getSubject().equals("New Test Update Message"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(updatedMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    String mailSubject = "Test Delete Message";
                    Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                    Message addedMessage = client.getMe().getMessages().add(message).get();

                    //Act
                    client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(addedMessage.getId())
                            .delete().get();

                    Thread.sleep(1000);

                    //Assert
                    List<Message> messages = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages().read().get();

                    boolean exists = false;
                    for (Message m : messages) {
                        if (m.getId().equals(addedMessage.getId()))
                            exists = true;
                    }

                    if (exists)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canMoveMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    String destinationFolderName = "Inbox";

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    String mailSubject = "Test move Message";
                    Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                    Message addedMessage = client.getMe().getMessages().add(message).get();
                    //Act
                    Message movedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(addedMessage.getId())
                            .getOperations().move(destinationFolderName).get();

                    Thread.sleep(2000);
                    //Assert
                    try {
                        Message m = client.getMe()
                                .getFolders()
                                .getById(destinationFolderName)
                                .getMessages()
                                .getById(movedMessage.getId())
                                .read().get();

                        if (m == null && !m.getId().equals(movedMessage.getId()))
                            result.setStatus(TestStatus.Failed);
                    } catch (Throwable t) {
                        result.setStatus(TestStatus.Failed);
                    }


                    //Cleanup
                    client.getMe()
                            .getMessages()
                            .getById(movedMessage.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCopyMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    String destinationFolderName = "Inbox";

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    String mailSubject = "Test copy Message";
                    Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                    Message addedMessage = client.getMe().getMessages().add(message).get();

                    //Act
                    Message copiedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(addedMessage.getId())
                            .getOperations().copy(destinationFolderName).get();

                    Thread.sleep(2000);
                    //Assert
                    try {
                        Message m = client.getMe()
                                .getFolders()
                                .getById(destinationFolderName)
                                .getMessages()
                                .getById(copiedMessage.getId())
                                .read().get();

                        if (m == null && !m.getId().equals(copiedMessage.getId()))
                            result.setStatus(TestStatus.Failed);
                    } catch (Throwable t) {
                        result.setStatus(TestStatus.Failed);
                    }


                    //Cleanup
                    client.getMe()
                            .getMessages()
                            .getById(copiedMessage.getId())
                            .delete().get();

                    client.getMe()
                            .getMessages()
                            .getById(addedMessage.getId())
                            .delete().get();

                    return result;
                } catch (Throwable e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));

                    String stackTrace = e.toString();
                    Log.e("SDK-Error", stackTrace);
                    //return createResultFromException(e);

                    return createResultFromException(new Exception("Error in test execution", e));
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }


    private TestCase canCreateReplyMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    List<Message> inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    if(inboxMessages.size()== 0)
                    {
                        String mailSubject = "Test reply Message";
                        Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");
                        client.getMe().getOperations().sendMail(message, true).get();
                        Thread.sleep(2000);
                        inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    }

                    Message messageToReply = inboxMessages.get(0);
                    //Act
                    Message repliedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(messageToReply.getId())
                            .getOperations().createReply().get();

                    //Assert
                    List<Message> messages = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages().read().get();

                    boolean exists = false;
                    for(Message m : messages){
                        if(m.getConversationId().equals(messageToReply.getConversationId()))
                            exists = true;
                    }

                    if(!exists)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                            .getMessages()
                            .getById(messageToReply.getId())
                            .delete().get();

                    client.getMe()
                            .getMessages()
                            .getById(repliedMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateReplyAllMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    List<Message> inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    if(inboxMessages.size()== 0)
                    {
                        String mailSubject = "Test reply all Message";
                        Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");
                        client.getMe().getOperations().sendMail(message, true).get();
                        Thread.sleep(2000);
                        inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    }

                    Message messageToReply = inboxMessages.get(0);
                    //Act
                    Message repliedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(messageToReply.getId())
                            .getOperations().createReplyAll().get();

                    //Assert
                    List<Message> messages = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages().read().get();

                    boolean exists = false;
                    for(Message m : messages){
                        if(m.getConversationId().equals(messageToReply.getConversationId()))
                            exists = true;
                    }

                    if(!exists)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                            .getMessages()
                            .getById(messageToReply.getId())
                            .delete().get();

                    client.getMe()
                            .getMessages()
                            .getById(repliedMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateForwardMessage(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    List<Message> inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    if(inboxMessages.size()== 0)
                    {
                        String mailSubject = "Test fw Message";
                        Message message = getSampleMessage(mailSubject, ApplicationContext.getTestMail(), "");

                        client.getMe().getOperations().sendMail(message, true).get();
                        Thread.sleep(2000);
                        inboxMessages = client.getMe().getFolders().getById("Inbox").getMessages().top(1).read().get();
                    }

                    Message messageToReply = inboxMessages.get(0);
                    //Act
                    Message repliedMessage = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages()
                            .getById(messageToReply.getId())
                            .getOperations().createForward().get();

                    //Assert
                    List<Message> messages = client.getMe()
                            .getFolders()
                            .getById("Drafts")
                            .getMessages().read().get();

                    boolean exists = false;
                    for(Message m : messages){
                        if(m.getConversationId().equals(messageToReply.getConversationId()))
                            exists = true;
                    }

                    if(!exists)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                            .getMessages()
                            .getById(messageToReply.getId())
                            .delete().get();

                    client.getMe()
                            .getMessages()
                            .getById(repliedMessage.getId())
                            .delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private Message getSampleMessage(String subject, String to, String cc){
        Message m = new Message();
        // To Recipient
        final Recipient toRecipient = new Recipient();
        EmailAddress toEmailAddress = new EmailAddress();
        toEmailAddress.setAddress(to);
        toRecipient.setEmailAddress(toEmailAddress);
        ArrayList<Recipient> toRecipients = new ArrayList<Recipient>();
        toRecipients.add(toRecipient);
        m.setToRecipients(toRecipients);

        // CC recipient
        if(!cc.isEmpty()){
            final Recipient ccRecipient = new Recipient();
            EmailAddress ccEmailAddress = new EmailAddress();
            ccEmailAddress.setAddress(cc);
            ccRecipient.setEmailAddress(ccEmailAddress);
            ArrayList<Recipient> ccRecipients = new ArrayList<Recipient>();
            ccRecipients.add(ccRecipient);
            m.setCcRecipients(ccRecipients);
        }
        //Body and subject
        m.setSubject(subject);
        ItemBody body = new ItemBody();
        body.setContent("This is the email's body");
        m.setBody(body);

        return m;
    }

    FileAttachment getFileAttachment() {
        FileAttachment att = new FileAttachment();

        att.setContentBytes("hello world".getBytes());
        att.setName("myFile.txt");

        return att;
    }

    // Calendar Tests

    private TestCase canCreateCalendarGroup(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Act
                    CalendarGroup calendarGroup = new CalendarGroup();
                    calendarGroup.setName("My testing calendar Group");
                    CalendarGroup addedCalendarGroup = client.getMe()
                            .getCalendarGroups()
                            .add(calendarGroup).get();

                    //Assert
                    if(!addedCalendarGroup.getName().equals(calendarGroup.getName()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetCalendarGroups(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    CalendarGroup calendarGroup = new CalendarGroup();
                    calendarGroup.setName("My testing calendar Group");
                    CalendarGroup addedCalendarGroup = client.getMe()
                            .getCalendarGroups()
                            .add(calendarGroup).get();

                    // Act
                    List<CalendarGroup> calendarGroups = client.getMe().getCalendarGroups().read().get();

                    //Assert
                    if(calendarGroups.size() == 0)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetCalendarGroupById(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    CalendarGroup calendarGroup = new CalendarGroup();
                    calendarGroup.setName("My testing calendar Group");
                    CalendarGroup addedCalendarGroup = client.getMe()
                            .getCalendarGroups()
                            .add(calendarGroup).get();

                    // Act
                    CalendarGroup storedCalendarGroup = client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId()).read().get();

                    //Assert
                    if(!storedCalendarGroup.getName().equals(addedCalendarGroup.getName()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canUpdateCalendarGroup(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    CalendarGroup calendarGroup = new CalendarGroup();
                    calendarGroup.setName("My testing calendar Group");
                    CalendarGroup addedCalendarGroup = client.getMe()
                            .getCalendarGroups()
                            .add(calendarGroup).get();

                    // Act
                    calendarGroup.setName("Updated Calendar Group");
                    CalendarGroup updatedCalendarGroup = client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId())
                            .update(calendarGroup).get();

                    //Assert
                    if(!updatedCalendarGroup.getName().equals("Updated Calendar Group"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendarGroups()
                            .getById(updatedCalendarGroup.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteCalendarGroup(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    CalendarGroup calendarGroup = new CalendarGroup();
                    calendarGroup.setName("My testing calendar Group");
                    CalendarGroup addedCalendarGroup = client.getMe()
                            .getCalendarGroups()
                            .add(calendarGroup).get();

                    // Act
                    calendarGroup.setName("Updated Calendar Group");
                    client.getMe().getCalendarGroups()
                            .getById(addedCalendarGroup.getId())
                            .delete().get();

                    //Assert
                    try {
                        client.getMe().getCalendarGroups()
                                .getById(addedCalendarGroup.getId()).read().get();
                    }
                    catch (Exception e){
                        //It's supposed to fail
                        result.setStatus(TestStatus.Passed);
                    }

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetCalendars(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    String calendarName = "My testing calendar" + UUID.randomUUID().toString();
                    Calendar calendar = new Calendar();
                    calendar.setName(calendarName);
                    Calendar addedCalendar = client.getMe()
                            .getCalendars()
                            .add(calendar).get();

                    // Act
                    List<Calendar> calendars = client.getMe().getCalendars().read().get();

                    //Assert
                    if(calendars.size() == 0)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendars()
                            .getById(addedCalendar.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetDefaultCalendar(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Act
                    Calendar calendar = client.getMe().getCalendar().read().get();

                    //Assert
                    if(calendar.getName().equals(""))
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateCalendar(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Act
                    String calendarName = "My testing calendar" + UUID.randomUUID().toString();
                    Calendar calendar = new Calendar();
                    calendar.setName(calendarName);
                    Calendar addedCalendar = client.getMe()
                            .getCalendars()
                            .add(calendar).get();

                    //Assert
                    if(!addedCalendar.getName().equals(calendar.getName()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendars()
                            .getById(addedCalendar.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetCalendarById(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    String calendarName = "My testing calendar" + UUID.randomUUID().toString();
                    Calendar calendar = new Calendar();
                    calendar.setName(calendarName);
                    Calendar addedCalendar = client.getMe()
                            .getCalendars()
                            .add(calendar).get();

                    // Act
                    Calendar storedCalendar = client.getMe().getCalendars()
                            .getById(addedCalendar.getId()).read().get();

                    //Assert
                    if(!storedCalendar.getName().equals(addedCalendar.getName()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendars()
                            .getById(addedCalendar.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canUpdateCalendar(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    String calendarName = "My testing calendar" + UUID.randomUUID().toString();
                    Calendar calendar = new Calendar();
                    calendar.setName(calendarName);
                    Calendar addedCalendar = client.getMe()
                            .getCalendars()
                            .add(calendar).get();

                    // Act
                    String updatedCalendarName = "Updated Calendar" + UUID.randomUUID().toString();
                    calendar.setName(updatedCalendarName);
                    Calendar updatedCalendar = client.getMe().getCalendars()
                            .getById(addedCalendar.getId())
                            .update(calendar).get();

                    //Assert
                    if(!updatedCalendar.getName().equals(updatedCalendarName))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendars()
                            .getById(updatedCalendar.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteCalendar(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    String calendarName = "My testing calendar" + UUID.randomUUID().toString();
                    Calendar calendar = new Calendar();
                    calendar.setName(calendarName);
                    Calendar addedCalendar = client.getMe()
                            .getCalendars()
                            .add(calendar).get();

                    // Act
                    client.getMe().getCalendars()
                            .getById(addedCalendar.getId())
                            .delete().get();

                    //Assert
                    try {
                        client.getMe().getCalendars()
                                .getById(addedCalendar.getId()).read().get();
                    }
                    catch (Exception e){
                        //It's supposed to fail
                        result.setStatus(TestStatus.Passed);
                    }

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetEvents(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    Event event = getSampleEvent();
                    Event addedEvent = client.getMe().getCalendars().getById("Calendar").getEvents().add(event).get();

                    // Act
                    List<Event> events = client.getMe().getCalendars().getById("Calendar").getEvents().read().get();

                    //Assert
                    if(events.size() == 0)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getCalendars()
                            .getById("Calendar")
                            .getEvents()
                            .getById(addedEvent.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateEvents(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    Event event = getSampleEvent();

                    // Act
                    Event addedEvent = client.getMe().getCalendars().getById("Calendar").getEvents().add(event).get();

                    //Assert
                    if(!addedEvent.getSubject().equals(event.getSubject()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                            .getEvents()
                            .getById(addedEvent.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canUpdateEvents(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    Event event = getSampleEvent();
                    Event addedEvent = client.getMe().getCalendars().getById("Calendar").getEvents().add(event).get();

                    // Act
                    event.setSubject("Updated Subject");
                    event.setImportance(Importance.Low);

                    Event updatedEvent = client.getMe().getEvents().getById(addedEvent.getId()).update(event).get();

                    //Assert
                    if(updatedEvent.getImportance() != Importance.Low || !updatedEvent.getSubject().equals("Updated Subject"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                            .getEvents()
                            .getById(addedEvent.getId())
                            .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteEvents(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    Event event = getSampleEvent();
                    Event addedEvent = client.getMe().getCalendars().getById("Calendar").getEvents().add(event).get();

                    // Act
                    client.getMe()
                            .getEvents()
                            .getById(addedEvent.getId())
                            .delete().get();

                    //Assert
                    try {
                        client.getMe().getEvents()
                                .getById(addedEvent.getId()).read().get();
                    }
                    catch (Exception e){
                        //It's supposed to fail
                        result.setStatus(TestStatus.Passed);
                    }

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canRespondEvents(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    // Prepare
                    Event event = getSampleEvent();
                    Event addedEvent = client.getMe().getCalendars().getById("Calendar").getEvents().add(event).get();

                    // Act
                    Integer accepted = client.getMe()
                            .getEvents()
                            .getById(addedEvent.getId())
                            .getOperations().accept("Accepted").get();

                    //Assert
                    if(!addedEvent.getSubject().equals(event.getSubject()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe()
                        .getEvents()
                        .getById(addedEvent.getId())
                        .delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private Event getSampleEvent(){
        Event event = new Event();
        event.setSubject("Today's appointment");
        event.setStart(java.util.Calendar.getInstance());
        event.setImportance(Importance.High);

        //Event body
        ItemBody itemBody = new ItemBody();
        itemBody.setContent("This is the appointment info");
        itemBody.setContentType(BodyType.Text);

        event.setBody(itemBody);

        //Attendees
        Attendee attendee1 = new Attendee();
        EmailAddress email = new EmailAddress();
        email.setAddress(ApplicationContext.getTestMail());
        attendee1.setEmailAddress(email);
        List<Attendee> listAttendees = new ArrayList<Attendee>();
        listAttendees.add(attendee1);
        event.setAttendees(listAttendees);

        return event;
    }

    //Contacts
    private TestCase canGetContactsFolder(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Act
                    List<Contact> contacts = client.getMe()
                            .getContactFolders()
                            .getById("Contacts")
                            .getContacts().read().get();

                    //Assert
                    if(contacts == null)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canGetContacts(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Contact addedContact = client.getMe().getContacts().add(getContact()).get();

                    //Act
                    List<Contact> contacts = client.getMe().getContacts().top(2).read().get();

                    //Assert
                    if(contacts.size() == 0 || contacts.size() >2)
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getContacts().getById(addedContact.getId()).delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canCreateContact(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Contact addedContact = client.getMe().getContacts().add(getContact()).get();
                    Thread.sleep(2000);
                    //Act
                    Contact storedContact = client.getMe()
                            .getContacts()
                            .getById(addedContact.getId()).read().get();

                    //Assert
                    if(!storedContact.getId().equals(addedContact.getId()))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getContacts().getById(addedContact.getId()).delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canDeleteContact(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Contact addedContact = client.getMe().getContacts().add(getContact()).get();
                    Thread.sleep(2000);
                    //Act
                    client.getMe().getContacts().getById(addedContact.getId()).delete().get();

                    //Assert
                    List<Contact> contacts = client.getMe().getContacts().read().get();

                    boolean exists = false;
                    for(Contact c : contacts)
                    {
                        if(c.getId().equals(addedContact.getId()))
                            exists = true;
                    }

                    if(exists)
                        result.setStatus(TestStatus.Failed);

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canUpdateContact(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Passed);
                    result.setTestCase(this);

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Contact contact = getContact();
                    Contact addedContact = client.getMe().getContacts().add(contact).get();
                    contact.setGivenName("Updated given name");
                    //Act
                    client.getMe().getContacts().getById(addedContact.getId()).update(contact).get();
                    Thread.sleep(2000);
                    Contact updatedContact = client.getMe()
                            .getContacts()
                            .getById(addedContact.getId()).read().get();

                    //Assert
                    if(!updatedContact.getId().equals(addedContact.getId()) || !updatedContact.getGivenName().equals("Updated given name"))
                        result.setStatus(TestStatus.Failed);

                    //Cleanup
                    client.getMe().getContacts().getById(addedContact.getId()).delete().get();
                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private Contact getContact(){
        final Contact newContact = new Contact();
        newContact.setDisplayName("Test Contact");
        newContact.setGivenName("Test Contact Name");
        final EmailAddress email = new EmailAddress();
        email.setAddress("test@test.com");
        List<EmailAddress> list = new ArrayList<EmailAddress>();
        list.add(email);
        newContact.setEmailAddresses(list);

        return newContact;
    }

    // Filter, Select, Top, Skip
    private TestCase canFilterMessages(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    String subject = "Test Subject " + UUID.randomUUID().toString();
                    Message message = getSampleMessage(subject, ApplicationContext.getTestMail(), "");

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Message addedMessage = client.getMe().getFolders().getById("Drafts").getMessages().add(message).get();

                    //Act
                    java.util.Calendar dateGt = addedMessage.getDateTimeCreated();
                    dateGt.add(java.util.Calendar.SECOND, -2);

                    //format date properly
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSSSSS'Z'", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String formatted = dateFormat.format(dateGt.getTime());

                    List<Message> messages = client.getMe().getFolders().getById("Drafts").getMessages()
                            .filter("Subject eq '" + addedMessage.getSubject() + "' and DateTimeCreated gt " + formatted)
                            .read().get();

                    //Assert
                    if(messages.size() == 1)
                        result.setStatus(TestStatus.Passed);

                    //Cleanup
                    client.getMe().getMessages().getById(addedMessage.getId()).delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }

    private TestCase canSelectMessages(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    String subject = "Test Subject " + UUID.randomUUID().toString();
                    Message message = getSampleMessage(subject, ApplicationContext.getTestMail(), "");

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Message addedMessage = client.getMe().getFolders().getById("Drafts").getMessages().add(message).get();

                    //Act
                    List<Message> messages = client.getMe().getFolders().getById("Drafts").getMessages()
                            .filter("Subject eq '" + subject + "'")
                            .select("Subject,DateTimeCreated")
                            .read().get();

                    //Assert
                    if(messages.size() > 0 && !messages.get(0).getSubject().equals("") && messages.get(0).getDateTimeReceived() == null)
                        result.setStatus(TestStatus.Passed);

                    //Cleanup
                    client.getMe().getMessages().getById(addedMessage.getId()).delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }


    private TestCase canTopMessages(String name, boolean enabled) {
        TestCase test = new TestCase() {

            @Override
            public TestResult executeTest() {
                try {
                    TestResult result = new TestResult();
                    result.setStatus(TestStatus.Failed);
                    result.setTestCase(this);

                    String subject = "Test Subject " + UUID.randomUUID().toString();
                    Message message = getSampleMessage(subject, ApplicationContext.getTestMail(), "");

                    OutlookClient client = ApplicationContext.getMailCalendarContactClient();

                    //Prepare
                    Message addedMessage1 = client.getMe().getFolders().getById("Drafts").getMessages().add(message).get();
                    Message addedMessage2 = client.getMe().getFolders().getById("Drafts").getMessages().add(message).get();

                    //Act
                    List<Message> messages = client.getMe().getFolders().getById("Drafts").getMessages()
                            .filter("Subject eq '" + subject + "'")
                            .top(1)
                            .read().get();

                    //Assert
                    if(messages.size() == 1 && messages.get(0).getSubject().equals(subject))
                        result.setStatus(TestStatus.Passed);

                    //Cleanup
                    client.getMe().getMessages().getById(addedMessage1.getId()).delete().get();
                    client.getMe().getMessages().getById(addedMessage2.getId()).delete().get();

                    return result;
                } catch (Exception e) {
                    return createResultFromException(e);
                }
            }
        };

        test.setName(name);
        test.setEnabled(enabled);
        return test;
    }
}
