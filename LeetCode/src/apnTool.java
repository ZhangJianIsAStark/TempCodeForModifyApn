import java.io.*;

class apnTool {
    static void addProtocol(String path) {
        //get origin file
        File file = new File(path);

        if (file.exists() && file.isFile()) {
            //create a temp file
            File newFile = createNewFile(path);
            if (newFile == null) {
                System.out.println("couldn't create new file");
                return;
            }

            BufferedReader in = null;
            BufferedWriter out = null;
            try {
                in = new BufferedReader(new FileReader(file));
                out = new BufferedWriter(new FileWriter(newFile));

                String temp;
                boolean newApn = false;
                boolean hasProtocol = false;
                boolean hasRoamingProtocol = false;

                while ((temp = in.readLine()) != null) {
                    if (temp.contains("<apn")) {
                        newApn = true;
                        hasProtocol = false;
                        hasRoamingProtocol = false;
                    } else if (newApn) {
                        if (temp.contains("roaming_protocol")) {
                            hasRoamingProtocol = true;
                            if (!temp.contains("IPV4V6")) {
                                temp = modifyProtocolString(temp);
                            }
                        } else if (temp.contains("protocol")) {
                            hasProtocol = true;
                            if (!temp.contains("IPV4V6")) {
                                temp = modifyProtocolString(temp);
                            }
                        } else if (temp.contains("/>")) {
                            if (!hasProtocol) {
                                out.write("      protocol=\"IPV4V6\"\n");
                            }

                            if (!hasRoamingProtocol) {
                                out.write("      roaming_protocol=\"IPV4V6\"\n");
                            }

                            newApn = false;
                        }
                    }

                    out.write(temp + "\n");
                }
            } catch (Exception e) {
                //
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    //
                }
            }
        } else {
            System.out.println("open file failed");
        }
    }

    private static File createNewFile(String path) {
        File newFile = new File(path + ".tmp");
        boolean rst = true;
        if (!newFile.exists()) {
            try {
                rst = newFile.createNewFile();
            } catch (Exception e) {
                rst = false;
            }
        }
        if (rst) {
            return newFile;
        } else {
            return null;
        }
    }

    private static String modifyProtocolString(String curr) {
        int equal = curr.indexOf("=");
        return curr.substring(0, equal+1) + "\"IPV4V6\"";
    }

    static void checkProtocol(String path) {
        //get origin file
        File file = new File(path);

        if (file.exists() && file.isFile()) {
            BufferedReader in = null;
            int pass = 0;

            try {
                in = new BufferedReader(new FileReader(file));

                String temp;
                boolean newApn = false;
                int protocolCount = 0;
                int roamingCount = 0;

                while ((temp = in.readLine()) != null) {
                    if (temp.contains("<apn")) {
                        newApn = true;
                        protocolCount = 0;
                        roamingCount = 0;
                    } else if (newApn) {
                        if (temp.contains("roaming_protocol")) {
                            ++roamingCount;
                        } else if (temp.contains("protocol")) {
                            ++protocolCount;
                        } else if (temp.contains("/>")) {
                            newApn = false;
                            if (protocolCount != 1) {
                                System.out.println("protocol fail");
                            } else if (roamingCount != 1) {
                                System.out.println("roaming protocol fail");
                            } else {
                                ++pass;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                //
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    System.out.println("Pass: " + pass);
                } catch (Exception e) {
                    //
                }
            }
        }
    }
}
