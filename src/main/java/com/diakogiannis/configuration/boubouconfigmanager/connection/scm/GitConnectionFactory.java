/*
Copyright {2018} {Alexius Diakogiannis}

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
package com.diakogiannis.configuration.boubouconfigmanager.properties;
 */
package com.diakogiannis.configuration.boubouconfigmanager.connection.scm;

import java.io.ByteArrayOutputStream;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by Alexis-Dionisis on 17/3/2018.
 */
public class GitConnectionFactory {

    private static final Logger LOG = Logger.getLogger("com.diakogiannis.configuration.boubouconfigmanager.connection.scm.GitConnectionFactory");
    
    private String gitUri;
    private String gitBranch = "master";
    private String username;
    private String password;
    private String remotepath;

    /**
     * Default Constructor
     *
     * @param gitUri
     * @param gitBranch
     * @param username
     * @param password
     * @param remotepath
     */
    public GitConnectionFactory(String gitUri, String gitBranch, String username, String password, String remotepath) {
        this.gitUri = gitUri;
        this.gitBranch = gitBranch;
        this.username = username;
        this.password = password;
        this.remotepath = remotepath;
    }

    /**
     * Connects to Git server
     *
     * @return a Git client instance
     * @throws GitAPIException
     */
    public Repository connect() throws GitAPIException {

        Random random = new Random();
        File gitWorkDir = new File(System.getProperty("java.io.tmpdir") + Math.abs(random.nextInt()));
        gitWorkDir.mkdir();
        CloneCommand cloneCommand = Git.cloneRepository().setURI(gitUri).setDirectory(gitWorkDir).setBranch(gitBranch);
        if (username != null && password != null) {
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
        }

        return cloneCommand.call().getRepository();

    }

    public ByteArrayOutputStream readFile(Repository repository, String commitId, String filePath) {
 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            ObjectId lastCommitId = repository.resolve(commitId);
            RevWalk revWalk = new RevWalk(repository);
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(filePath));
            if (!treeWalk.next()) {
                throw new IllegalStateException("Did not find expected file "+filePath+" for commit ID "+commitId);
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);

            // and then one can the loader to read the file
            loader.copyTo(baos);
            revWalk.dispose();
        }catch (IncorrectObjectTypeException e) {
            LOG.severe(e.getMessage());
        } catch (AmbiguousObjectException e) {
            LOG.severe(e.getMessage());
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
        return baos;
    }

    public static void main(String[] args) {
        GitConnectionFactoryBuilder gitConnectionFactoryBuilder = new GitConnectionFactoryBuilder();
        GitConnectionFactory gitConnectionFactory = gitConnectionFactoryBuilder.setGitUri("https://github.com/diakogiannis/EasyDropboxFileHandler").build();
        
        try {

            Repository repository = gitConnectionFactory.connect();

            ByteArrayOutputStream baos = gitConnectionFactory.readFile(repository,Constants.HEAD,"README.md");
            LOG.info(baos.toString());
            /*
            
            
            ObjectId lastCommitId = repository.resolve(Constants.HEAD);
            RevWalk revWalk = new RevWalk(repository);
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create("README.md"));
            if (!treeWalk.next()) {
                throw new IllegalStateException("Did not find expected file 'README.md'");
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);

            // and then one can the loader to read the file
            loader.copyTo(System.out);

            revWalk.dispose();
*/
        } catch (GitAPIException e) {
            LOG.severe(e.getMessage());
        } 

    }

}
