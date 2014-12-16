using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace HomeCastXmlGenerator
{
    public partial class FormMain : Form
    {
        List<Serie> Serien;

        public FormMain()
        {
            InitializeComponent();
            labelPath.Text = "J:\\Serien";
        }

        private void button1_Click(object sender, EventArgs e)
        {
            DialogResult result = folderBrowserDialog1.ShowDialog();
            if (result == System.Windows.Forms.DialogResult.OK)
            {
                labelPath.Text = folderBrowserDialog1.SelectedPath;
            }
        }

        private void buttonGenerate_Click(object sender, EventArgs e)
        {
            Serien = new List<Serie>();
            
            foreach (string dir in Directory.GetDirectories(labelPath.Text))
            {
                Serie serie;
                Staffel staffel;
                Episode episode;

                DirectoryInfo dirInfo = new DirectoryInfo(dir);
                serie = new Serie();
                serie.titel = dirInfo.Name;

                foreach (string staffelDir in Directory.GetDirectories(dir))
                {
                    DirectoryInfo staffelInfo = new DirectoryInfo(staffelDir);
                    staffel = new Staffel();
                    staffel.number = GetNumberFromStaffelFolder(staffelInfo.Name);
                    staffel.episodeList = new List<Episode>();

                    int episodeCounter = 0;
                    foreach (string episodeFile in Directory.GetFiles(staffelDir))
                    {
                        episodeCounter += 1;
                        FileInfo episodeInfo = new FileInfo(episodeFile);
                        episode = new Episode();
                        episode.number = episodeCounter;
                        episode.path = GetNetworkPath(episodeInfo.FullName);

                        staffel.episodeList.Add(episode);
                    }
                    serie.staffelList.Add(staffel);
                }
                Serien.Add(serie);
            }

            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.IndentChars = "\t";
            using (XmlWriter writer = XmlWriter.Create(Path.Combine(labelPath.Text, "serien.xml"), settings))
            {
                writer.WriteStartDocument();
                writer.WriteStartElement("HomeCast");
                foreach (Serie serie in Serien)
                {
                    writer.WriteStartElement("serie");
                    writer.WriteAttributeString("name", serie.titel);

                    foreach (Staffel staffel in serie.staffelList)
                    {
                        writer.WriteStartElement("staffel");
                        writer.WriteAttributeString("nr", staffel.number.ToString());

                        foreach (Episode episode in staffel.episodeList)
                        {
                            writer.WriteStartElement("episode");
                            writer.WriteAttributeString("nr", episode.number.ToString());
                            writer.WriteAttributeString("path", episode.path);
                            writer.WriteEndElement();
                        }
                        writer.WriteEndElement();
                    }
                    writer.WriteEndElement();
                }
                writer.WriteEndElement();
                writer.WriteEndDocument();
            }
        }

        private string GetNetworkPath(string filePath)
        {
            return filePath;
        }

        private int GetNumberFromStaffelFolder(string dirName)
        {
            try
            {
                return int.Parse(dirName.Substring(8));
            }
            catch (Exception)
            {
                return 0;
            }
        }
    }
}
