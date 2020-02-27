package feast.core.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import feast.core.dao.ProjectRepository;
import feast.core.dao.UserRepository;
import feast.core.model.Project;
import feast.core.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.avro.generic.GenericData.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

public class AccessManagementServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private UserRepository userRepository;



  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private AccessManagementService accessManagementService;

  @Before
  public void setUp() {
    initMocks(this);
    accessManagementService = new AccessManagementService(projectRepository, userRepository);
  }

  @Test
  public void shouldCreateProjectIfItDoesntExist() {
    String project_name = "project1";
    Project project = new Project(project_name);
    when(projectRepository.saveAndFlush(any(Project.class))).thenReturn(project);
    accessManagementService.createProject(project_name);
    verify(projectRepository, times(1)).saveAndFlush(any());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateProjectIfItExist(){
    String project_name = "project1";
    when(projectRepository.existsById(project_name)).thenReturn(true);
    accessManagementService.createProject(project_name);
  }

  @Test
  public void shouldArchiveProjectIfItExists(){
    String project_name = "project1";
    when(projectRepository.findById(project_name)).thenReturn(Optional.of(new Project(project_name)));
    accessManagementService.archiveProject(project_name);
    verify(projectRepository, times(1)).saveAndFlush(any(Project.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotArchiveProjectIfItIsAlreadyArchived(){
    String project_name = "project1";
    when(projectRepository.findById(project_name)).thenReturn(Optional.empty());
    accessManagementService.archiveProject(project_name);
  }

  @Test
  public void shouldListProjects() {
    String project_name = "project1";
    Project project = new Project(project_name);
    List<Project> expected = Arrays.asList(project);
    when(projectRepository.findAllByArchivedIsFalse()).thenReturn(expected);
    List<Project> actual = accessManagementService.listProjects();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void shouldListMembers() {
    String project_name = "project1";
    User user = new User("user1");
    Set<User> expected = new HashSet<>(Arrays.asList(user));
    Project mockProject = mock(Project.class);
    when(projectRepository.findById(project_name)).thenReturn(Optional.of(mockProject));
    when(mockProject.getProjectMembers()).thenReturn(new HashSet<>(Arrays.asList(user)));
    Set<User> actual = accessManagementService.listMembers(project_name);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void addMember() {
  }

  @Test
  public void removeMember() {
  }
}